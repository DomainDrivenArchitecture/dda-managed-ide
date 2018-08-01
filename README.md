# dda-managed-ide
[![Clojars Project](https://img.shields.io/clojars/v/dda/dda-managed-ide.svg)](https://clojars.org/dda/dda-managed-ide)
[![Build Status](https://travis-ci.org/DomainDrivenArchitecture/dda-managed-ide.svg?branch=master)](https://travis-ci.org/DomainDrivenArchitecture/dda-managed-ide)

[![Slack](https://img.shields.io/badge/chat-clojurians-green.svg?style=flat)](https://clojurians.slack.com/messages/#dda-pallet/) | [<img src="https://domaindrivenarchitecture.org/img/meetup.svg" width=50 alt="DevOps Hacking with Clojure Meetup"> DevOps Hacking with Clojure](https://www.meetup.com/de-DE/preview/dda-pallet-DevOps-Hacking-with-Clojure) | [Website & Blog](https://domaindrivenarchitecture.org)

## Compatibility

This crate works with:
 * pallet 0.9
 * clojure 1.9
 * xubuntu 18.04

## Features

This crate builds on top of the dda-managed-vm to additionally provide a development environment.

[![Create a clojure ide in minutes](doc/video.png)](https://vimeo.com/247506291)

It crate automatically installs software on a Linux system. The target my be a standalone system, but in most cases we prefer virtual machines as they offer snapshots and eaysy relocation.

As this crate is build on top of the dda-managed-vm, all the software and tools that are installed by the vm, can also be installed with the ide. In our domain-layer we've chosen :virtualbox as target type. You may use our domain-layer as starting point also, but creating your own domain convention should be quite easy.

The following software/packages are installed additionally by dda-managed-ide:

 * clojure
  * lein with profiles & credentials
 * java
   * custom jdk & gradle
 * java-script
    * custom nodejs, npm & yarn
 * devops
   * docker-host with configuration for bridge networking
   * aws-cli with simple credentials configuration
   * mfa tool
   * terraform
   * packer
   * mach for terraform & dda-pallet integration
   * aws-amicleaner
 * atom with plugins for
   * clojure language & repl
   * java language
   * terraform, packer, json
 * intellij
   * configure inodes for idea
 * others
   * git with configuration
   * yed, argouml (uml / diagram)
   * dbvis (sql)
   * asciinema & animated gif generation
   * many more os-level tools like strace, iotop ...

## Usage documentation
This crate installs and configures software on your target system. You can provision pre-created virtual machines (see paragraph "Prepare vm" below), standalone systems or cloud instances.

### Prepare vm
If you want to use this crate, please ensure you meet the preconditions for the remote machine, i.e. xubuntu and openssh-server installed. If not yet installed, you may use the steps below:
1. Install xubuntu18.04
2. Login with your initial user and use:
```
sudo apt-get update
sudo apt-get upgrade
sudo apt-get install openssh-server
```
In case you want to install the software on the local machine rather than remote, you wouldn't need openssh-server but only a Java runtime environment. If not yet available, you can install Java by:
```
sudo apt-get install openjdk-11-jre-headless
```

### Usage Summary
1. Download the jar-file from the releases page of this repository (e.g. `curl -L -o managed-ide.jar https://github.com/DomainDrivenArchitecture/dda-managed-ide/releases/download/1.0.2/dda-managed-ide-1.0.2-standalone.jar`).
2. Deploy the jar-file on the source machine
3. Create the files `example-ide.edn` (Domain-Schema for your desktop) and `target.edn` (Schema for Targets to be provisioned) according to the reference and our example configurations. Please create them in the same folder where you've saved the jar-file. For more information about these files refer to the corresponding information below.
4. Start the installation:
```bash
java -jar managed-ide.jar --targets example-targets.edn example-ide.edn
```
If you want to install the ide on your localhost you don't need a target config.
```bash
java -jar managed-ide.jar example-ide.edn
```

### Configuration
The configuration consists of two files defining both WHERE to install the software and WHAT to install.
* `example-targets.edn`: describes on which target system(s) the software will be installed
* `example-ide.edn`: describes which software/packages will be installed

You can download examples of these configuration files from  
[example-targets.edn](https://github.com/DomainDrivenArchitecture/dda-managed-ide/blob/development/example-targets.edn) and
[example-ide.edn](https://github.com/DomainDrivenArchitecture/dda-managed-ide/blob/development/example-ide.edn) respectively.

#### Targets config example
Example content of the file, `example-targets.edn`:
```clojure
{:existing [{:node-name "test-vm1"            ; semantic name
             :node-ip "35.157.19.218"}]       ; the ip4 address of the machine to be provisioned
 :provisioning-user
 {:login "initial"                            ; account used to provision
  :password {:plain "secure1234"}}}           ; optional password, if no ssh key is authorized
```

#### IDE config example
Example content of the file, `example-ide.edn`:
```clojure
{:target-type :virtualbox
 :clojure {:lein-auth [{:repo "maven.my-repo.com"
                        :username {:plain "mvn-account"}
                        :password {:plain "mvn-password"}}]}
 :java {}
 :java-script {}
 :devops {:aws {:simple {:id {:plain "ACCESS_KEY"}
                         :secret {:plain "SECRET_KEY"}}}}
 :ide-platform #{:atom}
 :user {:name "test-user"
        :password {:plain "xxx"}
        :email "test-user@mydomain.org"
        :ssh {:ssh-public-key {:plain "rsa-ssh kfjri5r8irohgn...test.key comment"}
              :ssh-private-key {:plain "123Test"}}}
        :gpg {:gpg-public-key
              {:plain "-----BEGIN PGP ...."
               :gpg-private-key
               {:plain "-----BEGIN PGP ...."}
                :gpg-passphrase {:plain "passphrase"}}}}
```

The ide config defines the software/packages and user credentials of the newly created user to be installed.

### Watch log for debug reasons
In case of problems you may want to have a look at the log-file:
`less logs/pallet.log`

## Reference
Some details about the architecture: We provide two levels of API. **Domain** is a high-level API with many build in conventions. If this conventions don't fit your needs, you can use our low-level **infra** API and realize your own conventions.

### Targets
You can define provisioning targets using the [targets-schema](https://github.com/DomainDrivenArchitecture/dda-pallet-commons/blob/master/doc/existing_spec.md)

### Domain API
You can use our conventions as a smooth starting point:
[see domain reference](doc/reference_domain.md)

### Infra API
Or you can build your own conventions using our low level infra API. We will keep this API backward compatible whenever it will be possible:
[see infra reference](doc/reference_infra.md)

## License
Published under [apache2.0 license](LICENSE.md)
