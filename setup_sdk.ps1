$ProgressPreference = 'SilentlyContinue'
$latest = 'D:\Desktop\yizhi\android-sdk\cmdline-tools\latest'
New-Item -ItemType Directory -Force -Path "$latest\bin" | Out-Null
Copy-Item "$latest\*.bat" -Destination "$latest\bin\" -Force
Copy-Item "$latest\NOTICE.txt" -Destination "$latest\bin\" -Force -ErrorAction SilentlyContinue
Copy-Item "$latest\source.properties" -Destination "$latest\bin\" -Force -ErrorAction SilentlyContinue
Write-Host "DONE_SETUP"