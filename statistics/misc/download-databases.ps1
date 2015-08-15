$countryUrl = "http://geolite.maxmind.com/download/geoip/database/GeoLite2-Country.mmdb.gz"
$countryDbName = "country.mmdb.gz"
$cityUrl = "http://geolite.maxmind.com/download/geoip/database/GeoLite2-City.mmdb.gz"
$cityDbName = "city.mmdb.gz"
$testfolder = "..\src\test\resources\geolite"

if(-Not (Test-Path $testfolder)) {
    New-Item -Path $testfolder -ItemType Directory
}
Write-Host "Downloading database from "$countryUrl " Wait a little bit."
wget $countryUrl -OutFile $testfolder\$countryDbName

Write-Host "Downloading database from "$cityUrl " Wait a little bit."
wget $cityUrl -OutFile $testfolder\$cityDbName


. .\gzip.ps1

Write-Host "Expending databases to" $testfolder
Expand-GZip -FullName $testfolder\$countryDbName 
Expand-GZip -FullName $testfolder\$cityDbName 

pause

