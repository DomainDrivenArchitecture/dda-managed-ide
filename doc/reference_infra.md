The Infra configuration is a configuration on the infrastructure level of a crate. It contains the complete configuration options that are possible with the crate functions. You can find the details of the infra configurations at the other crates used:
* [dda-user-crate](https://github.com/DomainDrivenArchitecture/dda-user-crate)
* [dda-git-crate](https://github.com/DomainDrivenArchitecture/dda-git-crate)
* [dda-serverspec-crate](https://github.com/DomainDrivenArchitecture/dda-serverspec-crate)
* [dda-managed-vm](https://github.com/DomainDrivenArchitecture/dda-managed-vm)

For installation & configuration with the dda-managed-ide the schema is:
```clojure
(def ArgoUml
   {:version s/Str})

(def Yed
   {:download-url s/Str})

(def Dbvis
   {:version s/Str})

(def Basics
 {(s/optional-key :argo-uml) ArgoUml
  (s/optional-key :yed) Yed})

(def BasicsSettings
   #{:install-basics
     :install-asciinema})

; ----------------------- db --------------------------

(def Dbvis
  {:version s/Str})

(def Db
 {(s/optional-key :dbvis) Dbvis})

(def Settings
  #{:install-pgtools})


; ----------------------- clojure --------------------------

(def RepoAuth
 {:repo s/Str
  :username s/Str
  :password s/Str})

(def Clojure
 {(s/optional-key :signing-gpg-key) s/Str
  (s/optional-key :lein-auth) [RepoAuth]})

; ----------------------- java --------------------------

(def CustomJava
   {:tar-download-url s/Str
    :jdk-filesystem-name s/Str
    (s/optional-key :md5-hash) s/Str})

(def Gradle
   {:version s/Str})

(def Java
  {(s/optional-key :custom-java) CustomJava
   (s/optional-key :gradle) Gradle})

; ----------------------- java-script --------------------------

(def NodeJs
   s/Str) ; e.g. "6.16" "8.15" "9.11.2" "10.15.0"

(def JavaScript
   {:nodejs-install [NodeJs]
    :nodejs-use NodeJs})

(def JavaScriptSettings
  #{:install-yarn
    :install-npm
    :install-mach
    :install-asciinema})

; ----------------------- java-script --------------------------
(def PythonSettings
   #{:install-pip3
     :install-pybuilder
     :install-jupyterlab})

; ----------------------- devops --------------------------

(def Aws
   {(s/optional-key :simple) {:id s/Str
                              :secret s/Str}})

(def Terraform
   {:version s/Str
    (s/optional-key :sha256-hash) s/Str})

(def Packer
   {:version s/Str
    (s/optional-key :sha256-hash) s/Str})

(def Docker
   {:bip s/Str})

(def Devops {(s/optional-key :terraform) Terraform
             (s/optional-key :aws) Aws
             (s/optional-key :docker) Docker
             (s/optional-key :packer) Packer})

(def DevopsSettings
   #{:install-npm
     :install-mfa
     :install-mach
     :install-ami-cleaner})

; ----------------------- idea --------------------------

(def IdeaSettings
  #{:install-idea-inodes})

; ----------------------- atom --------------------------

(def Atom {(s/optional-key :plugins) [s/Str]})

(def AtomSettings
   #{:install-aws-workaround})

; ----------------------- all together --------------------------

(def DdaIdeConfig
  {:ide-user s/Keyword
   (s/optional-key :basics) Basics
   (s/optional-key :db) Db
   (s/optional-key :clojure) Clojure
   (s/optional-key :java) Java
   (s/optional-key :java-script) JavaScript
   (s/optional-key :devops) Devops
   (s/optional-key :atom) Atom
   :ide-settings
   (hash-set (apply s/enum
                    (clojure.set/union
                      BasicsSettings
                      PythonSettings
                      DevopsSettings
                      AtomSettings
                      IdeaSettings
                      JavaScriptSettings)))})
```
