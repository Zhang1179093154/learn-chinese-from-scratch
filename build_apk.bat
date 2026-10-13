@echo off
setlocal enabledelayedexpansion

set SDK_ROOT=D:\Desktop\yizhi\android-sdk
set BUILD_TOOLS=%SDK_ROOT%\build-tools\34.0.0
set PLATFORM=%SDK_ROOT%\platforms\android-34\android.jar
set JAVA_HOME=D:\JDK_21
set PROJECT=D:\Desktop\yizhi
set APP_SRC=%PROJECT%\app\src\main
set RES=%APP_SRC%\res
set JAVA_SRC=%APP_SRC%\java
set MANIFEST=%APP_SRC%\AndroidManifest.xml
set BUILD_DIR=%PROJECT%\build
set GEN_DIR=%BUILD_DIR%\gen
set CLASSES_DIR=%BUILD_DIR%\classes
set COMPILED_RES=%BUILD_DIR%\compiled_res
set OUTPUT_APK=%BUILD_DIR%\app-debug.apk
set ALIGNED_APK=%BUILD_DIR%\app-debug-aligned.apk
set SIGNED_APK=%BUILD_DIR%\yizhi-learn.apk
set KEYSTORE=%BUILD_DIR%\debug.keystore
set KEYSTORE_PASS=android
set KEY_ALIAS=androiddebugkey
set KEY_PASS=android

echo ====================================
echo Building YiZhi Learn APK
echo ====================================

REM Clean and create directories
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
mkdir "%BUILD_DIR%" 2>nul
mkdir "%GEN_DIR%" 2>nul
mkdir "%CLASSES_DIR%" 2>nul
mkdir "%COMPILED_RES%" 2>nul

REM Step 1: Generate R.java using aapt2 compile + link
echo [1/6] Compiling resources with aapt2...
"%BUILD_TOOLS%\aapt2.exe" compile --dir "%RES%" -o "%COMPILED_RES%\resources.zip"
if %ERRORLEVEL% neq 0 (
    echo ERROR: aapt2 compile failed
    exit /b 1
)

echo [2/6] Linking resources with aapt2...
"%BUILD_TOOLS%\aapt2.exe" link "%COMPILED_RES%\resources.zip" -I "%PLATFORM%" --manifest "%MANIFEST%" --java "%GEN_DIR%" -o "%BUILD_DIR%\base.apk" --auto-add-overlay
if %ERRORLEVEL% neq 0 (
    echo ERROR: aapt2 link failed
    exit /b 1
)

REM Step 2: Compile Java sources
echo [3/6] Compiling Java sources...
set CLASSPATH=%PLATFORM%
for /r "%GEN_DIR%" %%f in (*.java) do (
    set SOURCES=!SOURCES! "%%f"
)
for /r "%JAVA_SRC%" %%f in (*.java) do (
    set SOURCES=!SOURCES! "%%f"
)

"%JAVA_HOME%\bin\javac.exe" -d "%CLASSES_DIR%" -classpath "%CLASSPATH%" -source 1.8 -target 1.8 %SOURCES%
if %ERRORLEVEL% neq 0 (
    echo ERROR: javac failed
    exit /b 1
)

REM Step 3: Convert to DEX
echo [4/6] Converting to DEX with d8...
"%BUILD_TOOLS%\d8.bat" --lib "%PLATFORM%" --output "%BUILD_DIR%\classes.dex" %CLASSES_DIR%
if %ERRORLEVEL% neq 0 (
    echo ERROR: d8 failed
    exit /b 1
)

REM Step 4: Add DEX to APK
echo [5/6] Adding DEX to APK...
copy "%BUILD_DIR%\base.apk" "%OUTPUT_APK%" >nul
"%BUILD_TOOLS%\aapt2.exe" add "%OUTPUT_APK%" "%BUILD_DIR%\classes.dex"
if %ERRORLEVEL% neq 0 (
    echo ERROR: aapt2 add failed
    exit /b 1
)

REM Step 5: ZipAlign
echo [6/6] Aligning APK...
"%BUILD_TOOLS%\zipalign.exe" -f -p 4 "%OUTPUT_APK%" "%ALIGNED_APK%"
if %ERRORLEVEL% neq 0 (
    echo ERROR: zipalign failed
    exit /b 1
)

REM Step 6: Sign APK
echo [7/7] Signing APK...

REM Generate debug keystore if not exists
if not exist "%KEYSTORE%" (
    "%JAVA_HOME%\bin\keytool.exe" -genkeypair -v -keystore "%KEYSTORE%" -alias "%KEY_ALIAS%" -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Debug, OU=Debug, O=Debug, L=Debug, ST=Debug, C=CN" -storepass "%KEYSTORE_PASS%" -keypass "%KEY_PASS%" -noprompt
)

"%BUILD_TOOLS%\apksigner.bat" sign --ks "%KEYSTORE%" --ks-pass pass:"%KEYSTORE_PASS%" --ks-key-alias "%KEY_ALIAS%" --key-pass pass:"%KEY_PASS%" --out "%SIGNED_APK%" "%ALIGNED_APK%"
if %ERRORLEVEL% neq 0 (
    echo ERROR: apksigner failed
    exit /b 1
)

echo ====================================
echo APK BUILD SUCCESSFUL!
echo Output: %SIGNED_APK%
echo ====================================

endlocal