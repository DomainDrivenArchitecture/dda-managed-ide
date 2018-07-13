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
                   :usage-type :desktop-base}
   :git-domain {:user-email "test@mydomain",
                :repos {:books ["https://github.com/DomainDrivenArchitecture/ddaArchitecture.git"],
                        :dda-pallet ["https://github.com/DomainDrivenArchitecture/dda-config-commons.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-pallet-commons.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-pallet.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-user-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-tinc-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-hardening-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-provider-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-init-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-backup-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-mysql-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/httpd-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-httpd-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-tomcat-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-liferay-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-linkeddata-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-managed-vm.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-managed-ide.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-pallet-masterbuild.git"],
                        :password-store ["https://github.com/DomainDrivenArchitecture/password-store-for-teams.git"]},
                :os-user :test}
   :serverspec-domain {:file '({:path "/opt/leiningen/lein"}
                               {:path "/etc/profile.d/lein.sh"}
                               {:path "/home/test/.lein/profiles.clj"})}
   :infra {:dda-managed-ide {:ide-user :test,
                             :atom {:settings #{},
                                    :plugins ["ink" "minimap" "busy-signal" "atom-toolbar"
                                              "atom-meld" "intentions" "trailing-spaces"
                                              "linter" "linter-write-good" "linter-ui-default"
                                              "linter-jsonlint" "linter-spell" "linter-spell-html"
                                              "linter-clojure" "minimap-linter" "teletype" "proto-repl"
                                              "clojure-plus" "parinfer" "lisp-paredit" "linter-clojure"
                                              "tree-view-git-status" "git-time-machine"
                                              "language-diff" "split-diff"]}
                             :ide-settings #{:install-idea-inodes :install-basics
                                             :install-mfa :install-asciinema}}}})

(def config-set-2
  {:domain-input {:user {:name  "test"
                         :password "pwd"}
                  :target-type :virtualbox
                  :ide-platform #{:atom}
                  :clojure {}}
   :dda-vm-domain {:user {:name "test", :password "pwd"},
                   :target-type :virtualbox,
                   :usage-type :desktop-base}
   :git-domain {:user-email "test@mydomain",
                :repos {:books ["https://github.com/DomainDrivenArchitecture/ddaArchitecture.git"],
                        :dda-pallet ["https://github.com/DomainDrivenArchitecture/dda-config-commons.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-pallet-commons.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-pallet.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-user-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-tinc-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-hardening-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-provider-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-init-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-backup-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-mysql-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/httpd-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-httpd-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-tomcat-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-liferay-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-linkeddata-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-managed-vm.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-managed-ide.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-pallet-masterbuild.git"],
                        :password-store ["https://github.com/DomainDrivenArchitecture/password-store-for-teams.git"]},
                :os-user :test}
   :serverspec-domain {:file '({:path "/opt/leiningen/lein"}
                               {:path "/etc/profile.d/lein.sh"}
                               {:path "/home/test/.lein/profiles.clj"})}
   :infra {:dda-managed-ide {:ide-user :test,
                             :atom {:settings #{},
                                    :plugins ["ink" "minimap" "busy-signal" "atom-toolbar" "atom-meld"
                                              "intentions" "trailing-spaces" "linter" "linter-write-good"
                                              "linter-ui-default" "linter-jsonlint" "linter-spell"
                                              "linter-spell-html" "linter-clojure" "minimap-linter"
                                              "teletype" "proto-repl" "clojure-plus" "parinfer" "lisp-paredit"
                                              "linter-clojure" "tree-view-git-status"
                                              "git-time-machine" "language-diff" "split-diff",]}
                                   :clojure {:os-user-name "test"}
                             :ide-settings #{:install-idea-inodes :install-basics
                                             :install-mfa :install-asciinema}}}})

(def config-set-3
  {:domain-input {:user {:name  "test"
                         :password "pwd"}
                  :bookmarks [{:name "Bookmarks Toolbar"
                               :links [["url" "name"]]}]
                  :target-type :virtualbox
                  :ide-platform #{:atom}}
   :dda-vm-domain {:user {:name "test", :password "pwd"},
                   :target-type :virtualbox,
                   :usage-type :desktop-base}
   :git-domain {:user-email "test@mydomain",
                :repos {:books ["https://github.com/DomainDrivenArchitecture/ddaArchitecture.git"],
                        :dda-pallet ["https://github.com/DomainDrivenArchitecture/dda-config-commons.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-pallet-commons.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-pallet.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-user-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-tinc-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-hardening-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-provider-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-init-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-backup-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-mysql-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/httpd-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-httpd-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-tomcat-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-liferay-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-linkeddata-crate.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-managed-vm.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-managed-ide.git"
                                     "https://github.com/DomainDrivenArchitecture/dda-pallet-masterbuild.git"],
                        :password-store ["https://github.com/DomainDrivenArchitecture/password-store-for-teams.git"]},
                :os-user :test}
   :serverspec-domain {:file '({:path "/opt/leiningen/lein"}
                               {:path "/etc/profile.d/lein.sh"}
                               {:path "/home/test/.lein/profiles.clj"})}
   :infra {:dda-managed-ide {:ide-user :test,
                             :atom {:settings #{},
                                    :plugins ["ink" "minimap" "busy-signal" "atom-toolbar" "atom-meld"
                                              "intentions" "trailing-spaces" "linter" "linter-write-good"
                                              "linter-ui-default" "linter-jsonlint" "linter-spell"
                                              "linter-spell-html" "linter-clojure" "minimap-linter" "teletype"
                                              "proto-repl" "clojure-plus" "parinfer" "lisp-paredit"
                                              "linter-clojure" "tree-view-git-status"
                                              "git-time-machine" "language-diff" "split-diff"]}
                             :ide-settings #{:install-idea-inodes :install-basics
                                             :install-mfa :install-asciinema}}}})

(deftest test-git-config
  (testing
    "test the git config creation"
    (is (thrown? Exception (sut/ide-git-config {})))
    (is (= (:git-domain config-set-1)
           (sut/ide-git-config (:domain-input config-set-1))))
   (is (= (:git-domain config-set-2)
          (sut/ide-git-config (:domain-input config-set-2))))))

(deftest test-serverspec-config
  (testing
    "test the serverspec config creation"
    (is (thrown? Exception (sut/ide-serverspec-config {})))
    (is (= (:serverspec-domain config-set-1)
           (sut/ide-serverspec-config (:domain-input config-set-1))))
    (is (= (:serverspec-domain config-set-2)
           (sut/ide-serverspec-config (:domain-input config-set-2))))))

(deftest test-dda-vm-domain-config
  (testing
    "test the serverspec config creation"
    (is (thrown? Exception (sut/ide-serverspec-config {})))
    (is (= (:dda-vm-domain config-set-1)
           (sut/dda-vm-domain-configuration (:domain-input config-set-1))))
    (is (= (:dda-vm-domain config-set-2)
           (sut/dda-vm-domain-configuration (:domain-input config-set-2))))))

(deftest test-infra-configuration
  (testing
    "test the serverspec config creation"
    (is (thrown? Exception (sut/infra-configuration {})))
    (is (= (:infra config-set-1)
           (sut/infra-configuration (:domain-input config-set-1))))
    (is (= (:infra config-set-2)
           (sut/infra-configuration (:domain-input config-set-2))))
    (is (= (:infra config-set-3)
           (sut/infra-configuration (:domain-input config-set-3))))))
