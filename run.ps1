Get-Content .env | ForEach-Object { 
  if ($_ -match '^([^=]+)=(.*)$') { 
    [Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process') 
  } 
}
$env:JAVA_OPTS = "-Dcom.mongodb.driver.httpclient.tlsChannelType=netty -Djdk.tls.client.protocols=TLSv1.2"
& ./gradlew bootRun --args='--spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true'
