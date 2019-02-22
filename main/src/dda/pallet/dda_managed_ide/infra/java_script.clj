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

(ns dda.pallet.dda-managed-ide.infra.java-script
  (:require
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [selmer.parser :as selmer]
    [pallet.actions :as actions]
    [selmer.parser :as selmer]
    [dda.pallet.crate.util :as util]
    [dda.config.commons.user-home :as user-env]))

(def NodeJs
   s/Str) ; e.g. 6.x, 8.x or 10.x works

(def JavaScript
   {:nodejs-use NodeJs})

(def Settings
   #{:install-nvm
     :install-npm
     :install-mach
     :install-asciinema})

(s/defn
  init-nodejs
  [facility :- s/Keyword
   java-script :- JavaScript]
  (let [{:keys [nodejs-use]} java-script]
    (actions/as-action
      (logging/info (str facility "-init system: init-nodejs")))
    (actions/package-source (str "nodejs_" nodejs-use)
      :aptitude
      {:url (str "https://deb.nodesource.com/node_" nodejs-use)
       :release "bionic"
       :scopes ["main"]
       :key-url "https://deb.nodesource.com/gpgkey/nodesource.gpg.key"})))

(s/defn init-asciinema
  [facility :- s/Keyword]
  (actions/as-action
    (logging/info (str facility "-init system: init-asciinema")))
  (actions/package-source "asciinema"
    :aptitude
    {:url "http://ppa.launchpad.net/zanchey/asciinema/ubuntu "
     :release "bionic"
     :scopes ["main"]
     :key-url "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x9D2E234C0F833EAD"}))

(defn install-npm
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: install-npm")))
  (actions/packages
    :aptitude ["npm"]))

(s/defn
  install-nodejs
  "get and install install-nodejs"
  [facility :- s/Keyword
   nodejs :- NodeJs]
  (let [{:keys [nodejs-use]} nodejs]
    (actions/as-action
      (logging/info (str facility "-install system: install-nodejs")))
    (actions/packages :aptitude ["nodejs"])))

(s/defn
  install-user-nvm
  "get and install install-nodejs"
  [facility :- s/Keyword
   user-name :- s/Str]
  (let [user-home (user-env/user-home-dir user-name)]
    (actions/as-action
      (logging/info (str facility "-install user: install-user-nvm")))
    (actions/remote-file
     (str user-home "/.bashrc.d/nvm.sh")
     :literal true
     :content (selmer/render-file "js_nvm_bashrc.template" {})
     :owner user-name
     :group user-name)
    (actions/remote-file
     (str "/tmp/nvm_install.sh")
     :literal true
     :content (selmer/render-file "nvm_install.sh.template" {})
     :owner user-name
     :group user-name
     :mode "644")
    (actions/exec-checked-script
      "install-user-nvm"
      ("su" ~user-name "-c" "\"bash /tmp/nvm_install.sh\"")
      ("su" ~user-name "-c" ~(str "\"source " user-home "/.bashrc.d/nvm.sh\"")))))

(s/defn install-asciinema
  [facility :- s/Keyword]
  (actions/as-action
    (logging/info (str facility "-install-system: install-asciinema")))
  (actions/packages :aptitude ["asciinema" "imagemagick" "gifsicle"])
  (actions/exec-checked-script
    "install asciicast2gif"
    ("npm" "install" "--global" "phantomjs-prebuilt" "--unsafe-perm=true" "--allow-root")
    ("npm" "install" "--global" "asciicast2gif" "--unsafe-perm=true" "--allow-root")))

(defn install-mach
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: install-mach")))
  (actions/exec-checked-script
    "install mach"
    ("npm" "install" "--global" "@juxt/mach" "--unsafe-perm=true" "--allow-root")
    ("cd" "/usr/local/bin")
    ("curl" "-fsSLo" "boot"
            "https://github.com/boot-clj/boot-bin/releases/download/latest/boot.sh")
    ("chmod" "755" "boot")))

(s/defn init-system
  [facility :- s/Keyword
   contains-java-script? :- s/Bool
   js :- JavaScript
   settings]
  (let [{:keys [nodejs]} js]
    (when contains-java-script?
      (init-nodejs facility js))
    (when (contains? settings :install-asciinema)
       (init-asciinema facility))))

(s/defn install-system
  [facility :- s/Keyword
   contains-java-script? :- s/Bool
   js :- JavaScript
   settings]
  (let [{:keys [nodejs]} js]
    (when contains-java-script?
      (install-nodejs facility js))
    (when (contains? settings :install-npm)
      (install-npm facility))
    (when (contains? settings :install-asciinema)
       (install-asciinema facility))
    (when (contains? settings :install-mach)
      (install-mach facility))))

(s/defn install-user
  [facility :- s/Keyword
   os-user-name :- s/Str
   contains-java-script? :- s/Bool
   js :- JavaScript
   settings]
  (when (contains? settings :install-nvm)
    (install-user-nvm facility os-user-name)))
