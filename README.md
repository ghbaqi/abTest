#### themis_admin 虎扑AbTest后台配置系统

```
缓存同步，主要分三块
1、缓存分端层数据，用set存储  key: [ terminal:{CLIENT} ]  value(Set<String>): [ {layerId} ]
2、缓存白名单数据，用hash存储 key: [ wl:CLIENT:{layerId} ] field: [ clt ] value: [ k:paramKey,v:paramValue ]
3、缓存桶对应的参数信息，用string存储 key: [ bi:{CLIENT}:{layerId}:{bucketId} ]  value: [ { k:paramKey,v:paramValue } ]


查找逻辑代码

String clt 
String terminal

Map<String,String> result = new HashMap()
List<Long> layerIds = get("terminal:CLIENT")
for(Long layerId:layerIds){
   String entry = hmget("wl:CLIENT:{layerId}",clt)
   if(entry == null){
     bucketId = hash({layerId},clt)%10000 + 1
     entry = get("bi:{CLIENT}:{layerId}:{bucketId}")
     arr = entry.split(";")
     result.put(arr[0],arr[1])
   }else{
     arr = entry.split(";")
     result.put(arr[0],arr[1])
   }
}

return reuslt.toJson
```


#### themis_admin 构建
```
mvn clean install assembly:assembly -Dmaven.test.skip=true

打包完成后会生成 bd-themis_admin-svc-1.0.0-bin.zip
其中1.0.0 为版本号会更新
解压后 bin、lib、conf 三层目录

启动脚本为： sh bin/AppLaunch.sh --spring.profiles.active=prd

其中spring.profiles.active 用来指定环境



systemctl 启动命令
[Unit]
Description=bd-themis_admin-svc
After=network.target

[Service]
Type=simple
User=www
Group=www
WorkingDirectory=/data/www-data/hupu.com/bd-themis_admin-svc
ExecStart=/bin/bash  /data/www-data/hupu.com/bd-themis_admin-svc/bin/AppLaunch --spring.profiles.active=prd
ExecReload=/bin/kill -HUP $MAINPID
Restart=on-failure
RestartSec=10
Restart=always
LimitNOFILE=32768
LimitNPROC=32768

[Install]
WantedBy=multi-user.target
```