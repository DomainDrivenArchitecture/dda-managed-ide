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
(ns dda.pallet.dda-managed-vm.app.passwordstore-config
  (:require
    [dda.config.commons.user-env :as user-env]
    [dda.pallet.commons.passwordstore-adapter :as adapter]))

(def provisioning-ip
  "192.168.56.103")

(def provisioning-user
  {:login "initial"
   :password "secure1234"})

(defn ssh-pub-key [user-name]
  (user-env/string-to-pub-key-config
    (adapter/get-secret-wo-newline (str "meissa/" user-name "/system/ssh/id_rsa_pub"))))

(defn ssh-priv-key [user-name]
  (adapter/get-secret (str "meissa/" user-name "/system/ssh/id_rsa_priv")))

(defn gpg-public-key [user-name]
  (slurp (str (System/getenv "HOME") "/.password-store/meissa/public-keys/" user-name ".pub")))

(defn gpg-private-key [user-name]
  (adapter/get-secret (str "meissa/" user-name "/system/gpg/key.priv")))

(defn gpg-passphrase [user-name]
  (adapter/get-secret-wo-newline (str "meissa/" user-name "/system/gpg/key.passphrase")))

(defn password [user-name]
  (adapter/get-secret-wo-newline (str "meissa/" user-name "/system/password")))

(defn user-config [user-name]
   {(keyword user-name) {:clear-password (password user-name)
                         :personal-key {:public-key (ssh-pub-key user-name)
                                        :private-key (ssh-priv-key user-name)}
                         :authorized-keys [(ssh-pub-key user-name)]
                         :gpg {:trusted-key {:public-key (gpg-public-key user-name)
                                             :private-key (gpg-private-key user-name)
                                             :passphrase (gpg-passphrase user-name)}}}})

(defn vm-config [user-name]
  {:vm-user (keyword user-name)
   :platform :virtualbox
   :user-email (str user-name "@mydomain.org")})
