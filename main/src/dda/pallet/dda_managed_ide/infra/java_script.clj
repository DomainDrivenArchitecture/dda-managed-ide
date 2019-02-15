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
   s/Str) ; e.g. "6.16" "8.15" "9.11.2" "10.15.0"

(def JavaScript
   {:nodejs-install [NodeJs]
    :nodejs-use NodeJs})

(def Settings
   #{:install-yarn
     :install-npm
     :install-mach
     :install-asciinema})

(defn install-npm
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: install-npm")))
  (actions/packages
    :aptitude ["npm"]))

(s/defn
  install-user-nodejs
  "get and install install-nodejs"
  [facility :- s/Keyword
   os-user-name :- s/Str
   config :- NodeJs]
  (let [{:keys [nodejs-install nodejs-use]} config]
    (actions/as-action
      (logging/info (str facility "-install user: install-nodejs")))
    (actions/remote-file
     (str user-home "/.bashrc.d/gopass.sh")
     :literal true
     :content (selmer/render-file "js_nvm_bashrc.template" {})
     :owner user-name
     :group user-name)
    (actions/exec-checked-script
      "install-nodejs"
      ("curl" "-o-" "https://raw.githubusercontent.com/creationix/nvm/v0.34.0/install.sh" "|" "bash")
      ("export" "NVM_DIR=\"$HOME/.nvm\"")
      (doseq [x ~nodejs-install]
        ("nvm" "install" @x))
      ("nvm" "use" ~nodejs-use))))

(s/defn
  init-yarn
  [facility :- s/Keyword]
  (actions/as-action
    (logging/info (str facility "-init system: init-yarn")))
  (actions/package-source "yarn"
    :aptitude
    {:url "https://dl.yarnpkg.com/debian/"
     :release "stable"
     :scopes ["main"]
     :key-url "https://dl.yarnpkg.com/debian/pubkey.gpg"}))

(s/defn
  install-yarn
  "get and install install-yarn"
  [facility :- s/Keyword]
  (actions/as-action
    (logging/info (str facility "-install system: install-yarn")))
  (actions/packages :aptitude ["nodejs"]))

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
    (when (contains? settings :install-yarn)
      (init-yarn facility))
    (when (contains? settings :install-asciinema)
       (init-asciinema facility))))

(s/defn install-system
  [facility :- s/Keyword
   contains-java-script? :- s/Bool
   js :- JavaScript
   settings]
  (let [{:keys [nodejs]} js]
    (when (contains? settings :install-npm)
      (install-npm facility))
    (when (contains? settings :install-yarn)
      (install-yarn facility))
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
  (let [{:keys [nodejs]} js]
    (when contains-java-script?
      (when (contains? js :nodejs)
        (install-user-nodejs facility os-user-name nodejs)))))
