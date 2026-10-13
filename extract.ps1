$ProgressPreference = 'SilentlyContinue'
$zipPath = "D:\Desktop\yizhi\cmdline-tools.zip"
$destPath = "D:\Desktop\yizhi\android-sdk"
if (Test-Path $destPath) { Remove-Item -Recurse -Force $destPath }
New-Item -ItemType Directory -Force -Path $destPath | Out-Null
Expand-Archive -Path $zipPath -DestinationPath $destPath -Force
Write-Host "SUCCESS: Extracted to $destPath"
Get-ChildItem -Path $destPath -Recurse -Depth 1 | Select-Object FullName