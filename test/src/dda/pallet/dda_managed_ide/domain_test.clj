; Licensed to the Apache Software Foundation (ASF) under one
; or more contributor license agreements. See the NOTICE file
; distributed with this work for additional information
; regarding copyright ownership. The ASF licenses this file
; to you under the Apache License, Version 2.0 (the
; "License"); you may not use this file except in compliance
; with the License. You may obtain a copy of the License at
;
; http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.


(ns dda.pallet.dda-managed-ide.domain-test
  (:require
    [clojure.test :refer :all]
    [schema.core :as s]
    [dda.pallet.dda-managed-ide.domain :as sut]))

(s/set-fn-validation! true)

(def config-set-1
  {:domain-input {:user {:name  "test"
                         :password "pwd"}
                  :target-type :remote-aws
                  :ide-platform #{:atom}}
   :dda-vm-domain {:user {:name "test", :password "pwd"},
                   :target-type :remote-aws,
                   :usage-type :desktop-ide}
   :git-domain {:user-email "test@mydomain",
                :repos {:dda-pallet ["https://github.com/DomainDrivenArchitecture/dda-config-commons.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-pallet-commons.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-pallet.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-user-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-backup-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-git-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-hardening-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/httpd-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-httpd-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-liferay-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-managed-vm.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-managed-ide.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-mariadb-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-serverspec-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-tomcat-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-cloudspec.git",]}
                :os-user :test}
   :serverspec-domain {:package
                          '({:name "atom"} {:name "python"} {:name "gvfs-bin"})}
   :infra {:dda-managed-ide {:ide-user :test,
                             :ide-settings #{:install-idea-inodes
                                             :install-basics
                                             :install-npm
                                             :install-asciinema}
                             :basics
                                   {:argo-uml {:version "0.34"},
                                    :yed
                                    {:download-url
                                     "https://www.yworks.com/resources/yed/demo/yEd-3.18.1.zip",}
                                    :dbvis {:version "10.0.13"}}
                             :atom {:plugins ["ink"
                                              "minimap"
                                              "busy-signal"
                                              "atom-toolbar"
                                              "atom-meld"
                                              "intentions"
                                              "trailing-spaces"
                                              "linter"
                                              "linter-write-good"
                                              "linter-ui-default"
                                              "linter-jsonlint"
                                              "linter-spell"
                                              "linter-spell-html"
                                              "linter-clojure"
                                              "minimap-linter"
                                              "teletype"
                                              "tree-view-git-status"
                                              "git-time-machine"
                                              "language-diff"
                                              "split-diff"]}}}})

(def config-set-clojure
  {:domain-input {:user {:name  "test"
                         :password "pwd"}
                  :target-type :virtualbox
                  :ide-platform #{:atom}
                  :clojure {:lein-auth [{:repo "maven.my-repo.com"
                                         :username "mvn-account"
                                         :password "mvn-password"}]}}
   :dda-vm-domain {:user {:name "test", :password "pwd"},
                   :target-type :virtualbox,
                   :usage-type :desktop-ide}
   :serverspec-domain {:file
                       '({:path "/home/test/.lein/profiles.clj"}
                         {:path "/opt/leiningen/lein"}
                         {:path "/etc/profile.d/lein.sh"},)
                       :package
                       '({:name "atom"} {:name "python"} {:name "gvfs-bin"})}
   :infra {:dda-managed-ide {:ide-user :test,
                             :ide-settings #{:install-idea-inodes
                                             :install-basics
                                             :install-npm
                                             :install-asciinema}
                             :basics
                                   {:argo-uml {:version "0.34"},
                                    :yed
                                    {:download-url
                                     "https://www.yworks.com/resources/yed/demo/yEd-3.18.1.zip",}
                                    :dbvis {:version "10.0.13"}}
                             :java {:java-default-to
                                    "/usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java"}
                             :atom {:plugins ["ink"
                                              "minimap"
                                              "busy-signal"
                                              "atom-toolbar"
                                              "atom-meld"
                                              "intentions"
                                              "trailing-spaces"
                                              "linter"
                                              "linter-write-good"
                                              "linter-ui-default"
                                              "linter-jsonlint"
                                              "linter-spell"
                                              "linter-spell-html"
                                              "linter-clojure"
                                              "minimap-linter"
                                              "teletype"
                                              "tree-view-git-status"
                                              "git-time-machine"
                                              "language-diff"
                                              "split-diff"
                                              "proto-repl"
                                              "clojure-plus"
                                              "parinfer"
                                              "lisp-paredit"
                                              "linter-clojure"]}
                             :clojure {:lein-auth [{:repo "maven.my-repo.com"
                                                    :username "mvn-account"
                                                    :password "mvn-password"}]}}}})

(def config-set-devops
  {:domain-input {:user {:name  "test"
                         :password "pwd"}
                  :bookmarks [{:name "Bookmarks Toolbar"
                               :links [["url" "name"]]}]
                  :target-type :virtualbox
                  :devops {:aws {:simple {:id "ACCESS_KEY"
                                          :secret "SECRET_KEY"}}}
                  :ide-platform #{:atom}}
   :dda-vm-domain {:user {:name "test", :password "pwd"},
                   :target-type :virtualbox,
                   :usage-type :desktop-ide}
   :serverspec-domain {:file '()}
   :infra {:dda-managed-ide {:ide-user :test,
                             :ide-settings #{:install-idea-inodes
                                             :install-basics
                                             :install-asciinema
                                             :install-npm
                                             :install-mfa
                                             :install-mach
                                             :install-ami-cleaner}
                             :basics
                                   {:argo-uml {:version "0.34"},
                                    :yed
                                    {:download-url
                                     "https://www.yworks.com/resources/yed/demo/yEd-3.18.1.zip",}
                                    :dbvis {:version "10.0.13"}}
                             :devops {:terraform
                                        {:version "0.11.7",
                                         :sha256-hash
                                         "6b8ce67647a59b2a3f70199c304abca0ddec0e49fd060944c26f666298e23418"}
                                      :packer
                                        {:version "1.2.5"
                                         :sha256-hash
                                         "bc58aa3f3db380b76776e35f69662b49f3cf15cf80420fc81a15ce971430824c"}
                                      :docker {:bip "192.168.1.1/24"}
                                      :aws {:simple {:id "ACCESS_KEY"
                                                     :secret "SECRET_KEY"}}}
                             :atom {:plugins ["ink"
                                              "minimap"
                                              "busy-signal"
                                              "atom-toolbar"
                                              "atom-meld"
                                              "intentions"
                                              "trailing-spaces"
                                              "linter"
                                              "linter-write-good"
                                              "linter-ui-default"
                                              "linter-jsonlint"
                                              "linter-spell"
                                              "linter-spell-html"
                                              "linter-clojure"
                                              "minimap-linter"
                                              "teletype"
                                              "tree-view-git-status"
                                              "git-time-machine"
                                              "language-diff"
                                              "split-diff"
                                              "language-terraform"
                                              "terraform-fmt"]}}}})

(def config-set-java
  {:domain-input {:user {:name  "test"
                         :password "pwd"}
                  :target-type :virtualbox
                  :java {}
                  :ide-platform #{:atom}}
   :serverspec-domain {:file '()}
   :infra {:dda-managed-ide
           {:ide-user :test,
            :ide-settings
            #{:install-basics :install-idea-inodes :install-npm :install-asciinema},
            :basics
            {:argo-uml {:version "0.34"},
             :yed
             {:download-url
              "https://www.yworks.com/resources/yed/demo/yEd-3.18.1.zip"},
             :dbvis {:version "10.0.13"}},
            :atom
            {:plugins
             ["ink"
              "minimap"
              "busy-signal"
              "atom-toolbar"
              "atom-meld"
              "intentions"
              "trailing-spaces"
              "linter"
              "linter-write-good"
              "linter-ui-default"
              "linter-jsonlint"
              "linter-spell"
              "linter-spell-html"
              "linter-clojure"
              "minimap-linter"
              "teletype"
              "tree-view-git-status"
              "git-time-machine"
              "language-diff"
              "split-diff"]},
            :java {:gradle {:version "4.9"}}}}})

(def config-set-js
  {:domain-input {:user {:name  "test"
                         :password "pwd"}
                  :target-type :virtualbox
                  :java-script {}
                  :ide-platform #{:atom}}
   :serverspec-domain {:file '()}
   :infra {:dda-managed-ide
           {:ide-user :test,
            :java-script
              {:nodejs {:version "10.x"}}
            :ide-settings
            #{:install-basics :install-idea-inodes :install-asciinema :install-yarn},
            :basics
            {:argo-uml {:version "0.34"},
             :yed
             {:download-url
              "https://www.yworks.com/resources/yed/demo/yEd-3.18.1.zip"},
             :dbvis {:version "10.0.13"}},
            :atom
            {:plugins
             ["ink"
              "minimap"
              "busy-signal"
              "atom-toolbar"
              "atom-meld"
              "intentions"
              "trailing-spaces"
              "linter"
              "linter-write-good"
              "linter-ui-default"
              "linter-jsonlint"
              "linter-spell"
              "linter-spell-html"
              "linter-clojure"
              "minimap-linter"
              "teletype"
              "tree-view-git-status"
              "git-time-machine"
              "language-diff"
              "split-diff"]},}}})

(deftest test-git-config
  (testing
    "test the git config creation"
    (is (thrown? Exception (sut/ide-git-config {})))
    (is (= (:git-domain config-set-1)
           (sut/ide-git-config (:domain-input config-set-1))))))

(deftest test-serverspec-config
  (testing
    "test the serverspec config creation"
    (is (thrown? Exception (sut/ide-serverspec-config {})))
    (is (= (:serverspec-domain config-set-1)
           (sut/ide-serverspec-config (:domain-input config-set-1))))
    (is (= (:serverspec-domain config-set-clojure)
           (sut/ide-serverspec-config (:domain-input config-set-clojure))))))

(deftest test-dda-vm-domain-config
  (testing
    "test the serverspec config creation"
    (is (thrown? Exception (sut/ide-serverspec-config {})))
    (is (= (:dda-vm-domain config-set-1)
           (sut/dda-vm-domain-configuration (:domain-input config-set-1))))
    (is (= (:dda-vm-domain config-set-clojure)
           (sut/dda-vm-domain-configuration (:domain-input config-set-clojure))))))

(deftest test-infra-configuration
  (testing
    "test the serverspec config creation"
    (is (thrown? Exception (sut/infra-configuration {})))
    (is (= (:infra config-set-1)
           (sut/infra-configuration (:domain-input config-set-1))))
    (is (= (:infra config-set-clojure)
           (sut/infra-configuration (:domain-input config-set-clojure))))
    (is (= (:infra config-set-devops)
           (sut/infra-configuration (:domain-input config-set-devops))))
    (is (= (:infra config-set-java)
           (sut/infra-configuration (:domain-input config-set-java))))
    (is (= (:infra config-set-js)
           (sut/infra-configuration (:domain-input config-set-js))))))
