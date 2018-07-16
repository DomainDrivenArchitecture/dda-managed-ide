# meissa-managed-vm

## mach
sudo apt-get install npm
sudo npm install -g @juxt/mach

sudo bash -c "cd /usr/local/bin && curl -fsSLo boot https://github.com/boot-clj/boot-bin/releases/download/latest/boot.sh && chmod 755 boot"

## terraform
apt install awscli
vi ~/.aws/credentials
[default]
aws_access_key_id = ACCESS_KEY
aws_secret_access_key = SECRET_KEY

curl -L -o /tmp/terraform_0.11.7_linux_amd64.zip https://releases.hashicorp.com/terraform/0.11.7/terraform_0.11.7_linux_amd64.zip
cd /tmp
unzip terraform_0.11.7_linux_amd64.zip
mv terraform /usr/local/bin/
