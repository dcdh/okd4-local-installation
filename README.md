# OKD 4 local installer

## What is it

**okd**, **local installation**, **libvirt**, **one services (dns, lb) machine**, **one bootstrap**, **one control plane (master)**, **one worker**

## How to

> build using **mvn clean install**
>
> run it by launching **build_and_run_local.sh**
>

## Issues

### Unable to push image into the docker registry used in *services* Virtual Machine.

> Affect all current OKD 4.6.x
>
> https://github.com/openshift/okd/issues/402

### Network master configuration with network ignition

> The master ignition use the merge feature to get a part of it from the bootstrap server.
> 
> The url used need to use the dns entry from the dns resolver present in *service* virtual machine.
>
> Static Network setup is defined in the ignition file.
> 
> ## Static network is not defined so virtual machines presents in the same network are not reachable. Hence, merge is not resolvable, not reachable.

```json
{
   "ignition":{
      "config":{
         "merge":[
            {
               "source":"https://api-int.sandbox.okd.local:22623/config/master",
               "verification":{
                  
               }
            }
         ],
         "replace":{
            "verification":{
               
            }
         }
      },
      "proxy":{
         
      },
      "security":{
         "tls":{
            "certificateAuthorities":[
               {
                  "source":"data:text/plain;charset=utf-8;base64,LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURFRENDQWZpZ0F3SUJBZ0lJQnIwWDR3RllVYWN3RFFZSktvWklodmNOQVFFTEJRQXdKakVTTUJBR0ExVUUKQ3hNSmIzQmxibk5vYVdaME1SQXdEZ1lEVlFRREV3ZHliMjkwTFdOaE1CNFhEVEl3TVRJeE5URTRNemN4TmxvWApEVE13TVRJeE16RTRNemN4Tmxvd0pqRVNNQkFHQTFVRUN4TUpiM0JsYm5Ob2FXWjBNUkF3RGdZRFZRUURFd2R5CmIyOTBMV05oTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFvK0JoMUNnTlZhVUgKakJpQnBZZzZMT1lJb21mR0xuZVZLbTI0d1pkdCt2NEZuRXRKcW54OGNDNEJvZzZ4SG5KMGl2bzgwR3IrSGhZZwozT0xOaFc2SGhnZkh1b2hOa0ZCVlpGUWFHWkowRWQ5L0diZGJnbnlSdkdPNmlYbVgwbmVQdmMrTG93RDEvK1JsCm9EU2pNNDR0UDA4OTYzVndTL0FkYmFXOEkwTy9XS1dpVFgxMVJreU1mZHkwNjBCRFIxd1ZvUUdTKzF6eVN4WS8KQ2N2YTRWUVR5QTRqbVhPWmNQZjFSSkNIajdJN3pwQytESkRBYVkrNHplbWtzRzAwc0dNb3lCU3hOR2lmSWlwQwpXWW1qNUZxWk1CNWl2VzFSaEFaRFBnUHBRQnN1c1B2dU94T0dIY0FBWTcrdVhsV1hSNk92MFUvNUpCeExveTNFCjBIYzN1dHV2WndJREFRQUJvMEl3UURBT0JnTlZIUThCQWY4RUJBTUNBcVF3RHdZRFZSMFRBUUgvQkFVd0F3RUIKL3pBZEJnTlZIUTRFRmdRVU9udjFXY3FZR29WMFMvWW4rVWxGVmM5WDN2Z3dEUVlKS29aSWh2Y05BUUVMQlFBRApnZ0VCQUl3UC9ucUhpbXFZVUdoRnljSEJoZXpnTytvUHEyNWhIQTlWelFpTEYzNThVazRzWlRZRGQ4bDFieU01CnMyT0cvRy9yNGEvWGNFWTFacVh0SEZleVhoRUJ3bUNZNjR1dDFLZlF1L2hKKzdqMXdnZC9qMGFRVEFHWGRBMGoKR2VjNitiK1FaK1RuL3NRZU9YdDJxb1kwZGNvMzhGWE0rcEFLVFZBMUhUUWIrYWN5RitJQS9BWlczU2tJc2QwSApFbFRhVDhFR2p2TG1sbW4wUmQwcVlaODBsNm5lTUNBUUdOZjNpanNxeFhGTVo5WDQzcHgyVzZjd0FtTzJZQlYrCnVjRm4zWDhOeUFENUFoQWo3N0ZqbkF2TVc4QXFwWnZ1MXYyMG1LUTVySVY3aVp6VHpMQmdISVRDQVpyWU92TmcKMWhkUU84NDNPelI3dHpSanFPNkIyMjNPQldvPQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==",
                  "verification":{
                     
                  }
               }
            ]
         }
      },
      "timeouts":{
         
      },
      "version":"3.1.0"
   },
   "passwd":{
      
   },
   "storage":{
      "files":[
         {
            "path":"/etc/NetworkManager/system-connections/ens3.nmconnection",
            "mode":384,
            "overwrite":true,
            "contents":{
               "source":"data:text/plain;charset=us-ascii;base64,W2Nvbm5lY3Rpb25dCmlkPWVuczMKdHlwZT1ldGhlcm5ldAppbnRlcmZhY2UtbmFtZT1lbnMzCltpcHY0XQphZGRyZXNzMT0xMC4wLjUuNTkKZ2F0ZXdheT0xMC4wLjUuMQpkbnM9MTAuMC41LjU3OzguOC44Ljg7OC44LjQuNDsKZG5zLXNlYXJjaD0KbWF5LWZhaWw9ZmFsc2UKbWV0aG9kPW1hbnVhbA=="
            }
         },
         {
            "path":"/etc/ssh/sshd_config.d/20-enable-passwords.conf",
            "mode":420,
            "overwrite":true,
            "contents":{
               "source":"data:text/plain;charset=us-ascii;base64,IyBGZWRvcmEgQ29yZU9TIGRpc2FibGVzIFNTSCBwYXNzd29yZCBsb2dpbiBieSBkZWZhdWx0LgojIEVuYWJsZSBpdC4KIyBUaGlzIGZpbGUgbXVzdCBzb3J0IGJlZm9yZSA0MC1kaXNhYmxlLXBhc3N3b3Jkcy5jb25mLgpQYXNzd29yZEF1dGhlbnRpY2F0aW9uIHllcw=="
            }
         }
      ]
   },
   "systemd":{
      
   }
}
```

> If the merge part is removed the virtual machine successfully boot with expected network setup.

![master_ignition_static_network_api_unreachable_screenshot](master_ignition_static_network_api_unreachable_screenshot.png)

![master_ignition_static_network_not_defined_screenshot](master_ignition_static_network_not_defined_screenshot.png)

> ### Need to use another way to define virtual machine static network with okd ignition content.
>