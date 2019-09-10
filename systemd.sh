#!/bin/bash
sudo apt install python3-pip python3 -y
pip3 install django --user

env>~/.env.file
sudo tee /etc/systemd/system/pdfall.service <<-'EOF'
[Unit]
Description=pdf-all Service
After=network-online.target
Wants=network-online.target systemd-networkd-wait-online.service

[Service]
EOF

sudo tee -a /etc/systemd/system/pdfall.service <<EOF
User=$(whoami)
;Group=$(id -g -n)
Type=simple
;ExecStartPre=/bin/bash --login -c 'env > /tmp/.new-env-file'
;EnvironmentFile=$HOME/.env.file
Environment="PYTHONPATH=$PYTHONPATH"
Environment="FUCK=hehe"
PrivateTmp=true
Restart=on-abnormal
ExecStart=$(cd "$(dirname "$0")";pwd)/server.sh
EOF

sudo tee -a /etc/systemd/system/pdfall.service <<-'EOF'
; Hide /home, /root, and /run/user. Nobody will steal your SSH-keys.
;ProtectHome=true
;after protect even if this script can not read home
; Make /usr, /boot, /etc and possibly some more folders read-only.
ProtectSystem=full

[Install]
WantedBy=multi-user.target
EOF
sudo systemctl daemon-reload
sudo systemctl enable pdfall.service
sudo systemctl restart pdfall.service
sudo systemctl status pdfall.service
