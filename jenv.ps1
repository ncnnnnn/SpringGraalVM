$oldpath = $env:Path
$json = "settings.json"
$cmd = "help"
# $args.Count
# $args[0]
function refreshenv {
    $x = $json 
    $SettingsObject = Get-Content -Path $x  | ConvertFrom-Json
    foreach ($obj in $SettingsObject.PSObject.Properties) {
        $obj.Name + " : " + $obj.Value
    }
    
}
if (($args.Count -eq 3) -and ($args[0] -eq "add") ) {
    $jdkpath = $args[1]
    $jdkname = $args[2]
    
    $x = $json 
    $json_data = Get-Content -Path $x  | ConvertFrom-Json

   
    if (!$json_data.psobject.properties.match($jdkname).Count) {
        # 如果不存在就增加
        $json_data | Add-Member -MemberType NoteProperty -Name $jdkname -Value   $jdkpath
    }
    else {
        # 如果存在就可以进行修改 
        $json_data.$jdkname = $jdkpath
    } 
   
    $json_data  | ConvertTo-Json -depth 10 | Set-Content -Path $json
    refreshenv
    exit
}
if (($args.Count -eq 2) -and ($args[0] -eq "set") ) {
    $jdkname = $args[1]
    $jdkname
    $x = $json 
    $json_data = Get-Content -Path $x  | ConvertFrom-Json 
    if (!$json_data.psobject.properties.match($jdkname).Count) {
        $jdkname + "不存在"
    }
    else {
        # $json_data  | ConvertTo-Json -depth 10 | Set-Content -Path $json
        $jdkpath = $json_data.$jdkname
        $jdkbinpath = Join-Path $jdkpath bin  
        $env:JAVA_HOME = $jdkpath
        $env:Path = "$jdkbinpath;$env:Path"
    } 
    
    exit
}
if (($args.Count -eq 2) -and ($args[0] -eq "del") ) {
    $jdk = $args[1]
    $x = $json 
    $SettingsObject = Get-Content -Path $x  | ConvertFrom-Json

    $SettingsObject.psobject.properties.remove($jdk)
   
    $SettingsObject  | ConvertTo-Json -depth 10 | Set-Content -Path $json
    refreshenv
    exit
}
if (($args.Count -eq 1) -and ($args[0] -eq "version") ) {
    refreshenv
    exit
}
if (  ($cmd -eq "help") ) {  
    
    # @REM " ================================================"
    # echo ================================================
    # @REM 
    " | JC-jEnv"
    
    " | Despt:  windows Java环境管理工具"
    
    " | Author: JC0o0l,Jerrybird"
    
    " | Repo:   https://github.com/chroblert/JC-jEnv.git"
    
    # @REM " ================================================"
    # echo ================================================
    
    " 使用说明"
    
    "  jenv [options]"
    
    "       version "
    
    "         - 显示当前所有的java版本"
    
    "       set alias "
    
    "         - 设置java版本，只在当前shell下起作用"
    
    
    "       add 目录 alias"
    
    "         - add a version "
    
    "       del alias     "
    
    "         - delete a version"
    exit
}