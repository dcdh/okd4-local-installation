#!/bin/sh
if [ ! -c /dev/kvm ]; then
 echo "Host where VirtualBox is running must have nested virtualization activated. Run the below command from host."
 echo "VBoxManage modifyvm CentosOKD --nested-hw-virt on"
 exit 1
fi
# tools
sudo dnf install net-tools vim -y
# virt
sudo dnf -y module install virt
sudo dnf install virt-install virt-viewer libguestfs-tools -y
sudo systemctl enable libvirtd.service
sudo systemctl start libvirtd.service
# cokpit
sudo dnf install cockpit cockpit-dashboard cockpit-podman cockpit-machines cockpit-networkmanager cockpit-packagekit cockpit-storaged -y
sudo systemctl start cockpit.socket
sudo systemctl enable cockpit.socket
sudo firewall-cmd --permanent --add-service=cockpit
sudo firewall-cmd --reload
# ovirt - not supported yet
#sudo dnf -y install https://resources.ovirt.org/pub/yum-repo/ovirt-release44.rpm
#sudo dnf module -y enable javapackages-tools
#sudo dnf module -y enable pki-deps
#sudo dnf module -y enable postgresql:12
#sudo dnf -y install vim tmux ovirt-engine
#echo "Run 'sudo engine-setup' to setup ovirt"