<#
  重置本机 MySQL 8.0 的 root 密码（忘记密码时使用）

  原理：用 mysqld 的 --init-file 参数，在服务启动瞬间以最高权限执行一条
        ALTER USER 语句改掉 root 密码，再恢复正常服务。

  用法（二选一）：
    1) 开始菜单搜索 "PowerShell" → 右键 → 以管理员身份运行
       然后执行：  cd "<本文件所在目录>"
                   .\reset-mysql-root-password.ps1
    2) 想自定义新密码：
       .\reset-mysql-root-password.ps1 -NewPassword "MyPass@2026"

  注意：必须【管理员权限】，因为要停止/启动 MySQL 服务。
#>

param(
    [string]$NewPassword = "Root@123456",
    [string]$ServiceName = "MySQL80",
    [string]$MySqlHome   = "C:\Program Files\MySQL\MySQL Server 8.0",
    [string]$MyIni       = "C:\ProgramData\MySQL\MySQL Server 8.0\my.ini"
)

$ErrorActionPreference = 'Stop'

function Say($msg, $color = 'Gray') { Write-Host $msg -ForegroundColor $color }

# ---------- 0. 权限检查 ----------
$identity  = [Security.Principal.WindowsIdentity]::GetCurrent()
$principal = New-Object Security.Principal.WindowsPrincipal($identity)
if (-not $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
    Say "❌ 需要管理员权限。" Red
    Say "   请用【以管理员身份运行】打开 PowerShell 后再执行本脚本。"
    exit 1
}
Say "✅ 管理员权限检查通过" Green

# ---------- 1. 路径检查 ----------
$mysqld = Join-Path $MySqlHome 'bin\mysqld.exe'
$mysql  = Join-Path $MySqlHome 'bin\mysql.exe'
foreach ($f in @($mysqld, $mysql, $MyIni)) {
    if (-not (Test-Path $f)) {
        Say "❌ 找不到文件：$f" Red
        Say "   如果你的安装路径不同，请用参数覆盖，例如："
        Say '   .\reset-mysql-root-password.ps1 -MySqlHome "D:\MySQL\MySQL Server 8.0" -MyIni "D:\MySQL\data\my.ini"'
        exit 1
    }
}
Say "✅ 找到 mysqld / mysql / my.ini"

# ---------- 2. 准备 init-file ----------
$initFile = Join-Path $env:TEMP 'mysql-reset-root.sql'
$sql = @"
ALTER USER 'root'@'localhost' IDENTIFIED BY '$NewPassword';
FLUSH PRIVILEGES;
"@
Set-Content -Path $initFile -Value $sql -Encoding ascii
Say "✅ 已生成临时重置脚本：$initFile"

$svc = Get-Service -Name $ServiceName -ErrorAction SilentlyContinue
if ($null -eq $svc) {
    Say "❌ 找不到服务 $ServiceName，请用 -ServiceName 参数指定正确的服务名" Red
    exit 1
}

# ---------- 3. 停服务 → 临时启动 mysqld 执行 init-file → 恢复服务 ----------
$started = $false
try {
    if ($svc.Status -eq 'Running') {
        Say "⏹  停止服务 $ServiceName ..."
        Stop-Service -Name $ServiceName -Force
        Start-Sleep -Seconds 3
    }

    Say "⏳ 以 --init-file 方式临时启动 mysqld（约需 12 秒）..."
    $argLine = "--defaults-file=`"$MyIni`" --init-file=`"$initFile`""
    Start-Process -FilePath $mysqld -ArgumentList $argLine -WindowStyle Hidden
    $started = $true
    Start-Sleep -Seconds 12
}
finally {
    if ($started) {
        Say "⏹  关闭临时 mysqld 进程 ..."
        Get-Process -Name mysqld -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 3
    }
    Remove-Item $initFile -Force -ErrorAction SilentlyContinue
    Say "▶  重新启动服务 $ServiceName ..."
    Start-Service -Name $ServiceName
    Start-Sleep -Seconds 5
}

# ---------- 4. 验证 ----------
Say "🔍 用新密码验证登录 ..."
$result = & $mysql -u root "-p$NewPassword" -e "SELECT VERSION() AS version;" 2>&1
if ($LASTEXITCODE -eq 0) {
    Say ""
    Say "✅ 密码重置成功！" Green
    Say "   用户名：root"
    Say "   密  码：$NewPassword"
    Say ""
    Say "   请把 backend/src/main/resources/application.yml 里的 password 改成这个："
    Say "       password: $NewPassword"
    Say ""
    Say "   然后回到 IDEA 里运行后端即可。"
} else {
    Say ""
    Say "❌ 验证失败，mysqld 返回：" Red
    $result | ForEach-Object { Say "   $_" }
    Say ""
    Say "   常见原因："
    Say "   1) 新密码太简单，被 validate_password 策略拒绝"
    Say "      → 换一个更复杂的：.\reset-mysql-root-password.ps1 -NewPassword 'Abc@12345'"
    Say "   2) 服务没起来 → 检查 'Get-Service $ServiceName' 的状态"
    exit 1
}
