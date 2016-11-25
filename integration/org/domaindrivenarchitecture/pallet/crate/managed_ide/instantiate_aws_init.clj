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
(ns org.domaindrivenarchitecture.pallet.crate.managed-ide.instantiate-aws-init
  (:require
    [clojure.java.io :as io]
    [pallet.api :as api]      
    [pallet.compute :as compute]
    [pallet.compute.node-list :as node-list]
    [org.domaindrivenarchitecture.pallet.commons.encrypted-credentials :as crypto]
    [org.domaindrivenarchitecture.pallet.commons.session-tools :as session-tools]
    [org.domaindrivenarchitecture.pallet.crate.config.node :as node-record]
    [org.domaindrivenarchitecture.pallet.crate.user.ssh-key :as ssh-key-record]
    [org.domaindrivenarchitecture.pallet.crate.config :as config]
    [org.domaindrivenarchitecture.pallet.crate.init :as init]
    [org.domaindrivenarchitecture.pallet.crate.managed-vm :as managed-vm]
    [org.domaindrivenarchitecture.pallet.core.cli-helper :as cli-helper])
  (:gen-class :main true))
 
(defn dda-read-file 
  "reads a file if it exists"
  [file-name]
  (if (.exists (io/file file-name))
    (slurp file-name)
    nil))

(def ssh-keys
  {:my-key
   (ssh-key-record/new-ssh-key
     (dda-read-file (str (System/getenv "HOME") "/.ssh/id_rsa.pub"))
     (dda-read-file (str (System/getenv "HOME") "/.ssh/id_rsa")))
   })

(def os-user
  {:root   {:authorized-keys [:my-key]}
   :pallet {:authorized-keys [:my-key]}
   :vmuser {:encrypted-password "TMctxnmttcODk" ; pw=test
            :authorized-keys [:my-key]
            :personal-key :my-key}
   })

(def meissa-vm
  (node-record/new-node 
    :host-name "my-ide" 
    :domain-name "meissa-gmbh.de"
    :additional-config 
    {:dda-managed-vm
     {:ide-user :vmuser}})
  )

(def config
  {:ssh-keys ssh-keys
   :os-user os-user
   :node-specific-config {:meissa-vm meissa-vm}
   })

(defn aws-node-spec []
  (api/node-spec
    :location {:location-id "eu-central-1a"
               ;:location-id "eu-west-1b"
               ;:location-id "us-east-1a"
               }
    :image {:os-family :ubuntu 
            ;eu-central-1 
            :image-id "ami-87564feb"
            ;us-east-1 :image-id "ami-2d39803a"
            ;eu-west1 :image-id "ami-f95ef58a"
            :os-version "14.04"
            :login-user "ubuntu"}
    :hardware {:hardware-id "t2.micro"}
    :provider {:pallet-ec2 {:key-name "jem"               
                            :network-interfaces [{:device-index 0
                                                  :groups ["sg-0606b16e"]
                                                  :subnet-id "subnet-f929df91"
                                                  :associate-public-ip-address true
                                                  :delete-on-termination true}]}}))

(defn aws-provider [key-id key-passphrase]
  (let 
    [aws-encrypted-credentials (get-in (pallet.configure/pallet-config) [:services :aws])
     aws-decrypted-credentials (crypto/decrypt
                                 (crypto/get-secret-key
                                   {:user-home "/home/mje/"
                                    :key-id key-id})
                                 aws-encrypted-credentials
                                 key-passphrase)]
    (compute/instantiate-provider
     :pallet-ec2
     :identity (get-in aws-decrypted-credentials [:account])
     :credential (get-in aws-decrypted-credentials [:secret])
     :endpoint "eu-central-1"
     :subnet-ids ["subnet-f929df91"])))

(defn managed-ide-group []
  (api/group-spec
    "managed-vm-group"
    :extends [(config/with-config config) 
              init/with-init 
              managed-vm/with-dda-vm]
    :node-spec (aws-node-spec)
    :count 1))

(defn inspect-phase-plan []
  (session-tools/inspect-mock-server-spec
     init/with-init :init))
 
(defn do-sth [key-id key-passphrase] 
      (api/converge
        (managed-ide-group)
        :compute (aws-provider key-id key-passphrase)
        :phase '(:settings :init)
        :user (api/make-user "ubuntu")))
