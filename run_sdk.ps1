$ProgressPreference = 'SilentlyContinue'
$sdkRoot = 'D:\Desktop\yizhi\android-sdk'
$toolsDir = "$sdkRoot\cmdline-tools\latest"
$classpath = "$toolsDir\lib\sdkmanager-classpath.jar"

$javaArgs = @(
    "-Dcom.android.sdklib.toolsdir=$toolsDir",
    '-classpath', $classpath,
    'com.android.sdklib.tool.sdkmanager.SdkManagerCli',
    '--sdk_root=' + $sdkRoot,
    '--list'
)

& java $javaArgs 2>&1 | Select-Object -First 50