$env:TRAE_USER_CLOUDIDE_TOKEN_BLOB = ''

$SDK_ROOT = 'D:\Desktop\yizhi\android-sdk'
$BUILD_TOOLS = "$SDK_ROOT\build-tools\34.0.0"
$PLATFORM = "$SDK_ROOT\platforms\android-34\android.jar"
$JAVA_HOME = 'D:\JDK_21'
$PROJECT = 'D:\Desktop\yizhi'
$APP_SRC = "$PROJECT\app\src\main"
$RES = "$APP_SRC\res"
$JAVA_SRC = "$APP_SRC\java"
$MANIFEST = "$APP_SRC\AndroidManifest.xml"
$BUILD_DIR = "$PROJECT\build"
$GEN_DIR = "$BUILD_DIR\gen"
$CLASSES_DIR = "$BUILD_DIR\classes"
$COMPILED_RES = "$BUILD_DIR\compiled_res"
$OUTPUT_APK = "$BUILD_DIR\app-debug.apk"
$ALIGNED_APK = "$BUILD_DIR\app-debug-aligned.apk"
$SIGNED_APK = "$BUILD_DIR\yizhi-learn.apk"
$KEYSTORE = "$BUILD_DIR\debug.keystore"
$KEYSTORE_PASS = 'android'
$KEY_ALIAS = 'androiddebugkey'
$KEY_PASS = 'android'

Write-Host "====================================="
Write-Host "Building YiZhi Learn APK"
Write-Host "====================================="

# Clean
if (Test-Path $BUILD_DIR) { Remove-Item -Recurse -Force $BUILD_DIR }
New-Item -ItemType Directory -Force -Path $GEN_DIR | Out-Null
New-Item -ItemType Directory -Force -Path $CLASSES_DIR | Out-Null
New-Item -ItemType Directory -Force -Path $COMPILED_RES | Out-Null

# Step 1: aapt2 compile
Write-Host "[1/6] Compiling resources with aapt2..."
$resZip = "$COMPILED_RES\resources.zip"
& "$BUILD_TOOLS\aapt2.exe" compile --dir $RES -o $resZip
if ($LASTEXITCODE -ne 0) { Write-Host "ERROR: aapt2 compile failed"; exit 1 }

# Step 2: aapt2 link
Write-Host "[2/6] Linking resources with aapt2..."
$baseApk = "$BUILD_DIR\base.apk"
& "$BUILD_TOOLS\aapt2.exe" link $resZip -I $PLATFORM --manifest $MANIFEST --java $GEN_DIR -o $baseApk --auto-add-overlay
if ($LASTEXITCODE -ne 0) { Write-Host "ERROR: aapt2 link failed"; exit 1 }

# Step 3: Compile Java
Write-Host "[3/6] Compiling Java sources..."
$javaFiles = @()
Get-ChildItem -Path $GEN_DIR -Filter *.java -Recurse | ForEach-Object { $javaFiles += $_.FullName }
Get-ChildItem -Path $JAVA_SRC -Filter *.java -Recurse | ForEach-Object { $javaFiles += $_.FullName }

$javaFilesStr = $javaFiles -join ' '
& "$JAVA_HOME\bin\javac.exe" -d $CLASSES_DIR -classpath $PLATFORM -source 1.8 -target 1.8 $javaFiles.Split(' ')
if ($LASTEXITCODE -ne 0) { Write-Host "ERROR: javac failed"; exit 1 }

# Step 3b: Create JAR from class files
Write-Host "[3b/6] Creating classes.jar..."
Push-Location $CLASSES_DIR
& "$JAVA_HOME\bin\jar.exe" cf "$BUILD_DIR\classes.jar" .
Pop-Location
if ($LASTEXITCODE -ne 0) { Write-Host "ERROR: jar creation failed"; exit 1 }

# Step 4: D8
Write-Host "[4/6] Converting to DEX with d8..."
& "$BUILD_TOOLS\d8.bat" --lib $PLATFORM --output $BUILD_DIR "$BUILD_DIR\classes.jar"
if ($LASTEXITCODE -ne 0) { Write-Host "ERROR: d8 failed"; exit 1 }

# Step 5: Add DEX to APK
Write-Host "[5/6] Adding DEX to APK..."
Copy-Item $baseApk $OUTPUT_APK -Force
Push-Location $BUILD_DIR
& "$JAVA_HOME\bin\jar.exe" uf $OUTPUT_APK classes.dex
Pop-Location
if ($LASTEXITCODE -ne 0) { Write-Host "ERROR: jar update failed"; exit 1 }

# Step 6: ZipAlign
Write-Host "[6/6] Aligning APK..."
& "$BUILD_TOOLS\zipalign.exe" -f -p 4 $OUTPUT_APK $ALIGNED_APK
if ($LASTEXITCODE -ne 0) { Write-Host "ERROR: zipalign failed"; exit 1 }

# Step 7: Sign
Write-Host "[7/7] Signing APK..."
if (-not (Test-Path $KEYSTORE)) {
    & "$JAVA_HOME\bin\keytool.exe" -genkeypair -v -keystore $KEYSTORE -alias $KEY_ALIAS -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Debug, OU=Debug, O=Debug, L=Debug, ST=Debug, C=CN" -storepass $KEYSTORE_PASS -keypass $KEY_PASS -noprompt
}

& "$BUILD_TOOLS\apksigner.bat" sign --ks $KEYSTORE --ks-pass "pass:$KEYSTORE_PASS" --ks-key-alias $KEY_ALIAS --key-pass "pass:$KEY_PASS" --out $SIGNED_APK $ALIGNED_APK
if ($LASTEXITCODE -ne 0) { Write-Host "ERROR: apksigner failed"; exit 1 }

Write-Host "====================================="
Write-Host "APK BUILD SUCCESSFUL!"
Write-Host "Output: $SIGNED_APK"
Write-Host "====================================="