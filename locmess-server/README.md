# Server ReadMe

To run the server on the [dply VPS](https://dply.co/) you must:
1. Have a github account
2. Add a ssh key to github
    * This key can be used to access the vps 
3. Create a new key value in https://keyvalue.xyz/
4. Change the parameters of the bash script below with:
    * The address of a file containing the server (in zip format)
    * The id of the key value
5. Create a new machine Debian 8 with the script below
    
 ```shell
#!/bin/sh
export DEBIAN_FRONTEND=noninteractive;

echo "Updating apt-get"
/usr/bin/apt-get update
wait
echo "Installing java 7"
/usr/bin/apt-get --assume-yes install openjdk-7-jdk
wait
echo "Installing sqlite3"
/usr/bin/apt-get --assume-yes install sqlite3
wait
echo "Installing unzip"
/usr/bin/apt-get --assume-yes install unzip 
wait
echo "Download Server"
wget -O /home/locmess-server.zip "DOWNLOAD_ADDRESS_HERE"
wait
#TODO add public key value
echo "Setup keyvalue with current ip"
serverip=`/sbin/ifconfig eth0 | grep "inet" | awk '{print $2}' | awk 'NR==1' | cut -d':' -f2`
curl -X POST https://api.keyvalue.xyz/KEY_VALUE_ID_HERE/locmessIP/$serverip
echo "Unzip server"
unzip /home/locmess-server.zip
chmod +x /home/gradlew
/home/gradlew bootRun
```

## Prepared VPS 
[![LocMess-Server](https://dply.co/b.svg)](https://dply.co/b/QCuCw4q0) 
