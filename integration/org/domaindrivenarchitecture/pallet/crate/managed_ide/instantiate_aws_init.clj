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
    [clojure.inspector :as inspector]
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
     (dda-read-file (str (System/getenv "HOME") "/.ssh/id_rsa.pub")))
   :matts-key 
   (ssh-key-record/new-ssh-key 
     "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQDQ/TFCz2q7cWrPqoWHDRUta0nO4qZin8AzB2Qmqq3jmS8rs7sFGOGcdgbL+/q8adEYOzVUHx6h6moKkwcrNsOj+Z/2LZxhxS/LVF9LICANaGsYK+30uSVCENYNGwG3fC+9imqiHs2+chRM2Nl9Q0qtAoOnUb22PDiKndYWG2tFrHK4bTQIf7zT8Y9DMdST/PohbvCAngc3ig7/YIWp+AeRegFgWq297EJrucEH8LyPWH9bja0zFM9Ywxt3hd0GQmPyuy2CxUoU1lvvX7IVo/3bK4DWrEETt04jA303uqOlqHwy4MGfkERLok21tkBeMJKV8rMrJBdmMmB+x6BlzbYtdz70Wu22M53nsJ9Ubr1ftyddnxceQfnmoEh3iqzt/ML09GDFxZO6ZDNKUqJdHGt+2lR1pSjehkRTtV6VWGdvxAyekg6sVam0LRyo7/SidbH5HjzYwAe3aaIAd/sepIyUOxxP3wd2GJl8jZy0czJUeqCFnxu5Fe6B2cYavYGyRZAq0mEduURbfjGhyQ6BmmOSMPCmLDOQbku0ugYFhxw/INNNaQE8kM/AmYE2ls4ZnpnO0hd9B6nR6J/jl4Xq080A4Sb1Yj6d7PLF5Fmk0/gmTMmQe2mG5QGXuLULUIVAbyTcFYlM1j1hPd1OrMutLIAiGcKsYntPKI0HjBQWRtjAtQ== matthew.r.lindsey@gmail.com")
   })

(def os-user
  {:root   {:authorized-keys [:my-key :matts-key]}
   :pallet {:authorized-keys [:my-key :matts-key]}
   :vmuser {:encrypted-password "TMctxnmttcODk" ; pw=test
            :authorized-keys [:my-key :matts-key]}
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
   :group-specific-config {:managed-ide-group meissa-vm}
   })

(defn aws-node-spec []
  (api/node-spec
    :location {:location-id "eu-central-1a"
               ;:location-id "eu-west-1b"
               ;:location-id "us-east-1a"
               }
    :image {:os-family :ubuntu 
            ;eu-central-1 16-04 LTS hvm 
            :image-id "ami-82cf0aed"
            ;eu-west1 16-04 LTS hvm :image-id "ami-07174474"
            ;us-east-1 16-04 LTS hvm :image-id "ami-45b69e52"
            :os-version "16.04"
            :login-user "ubuntu"}
    :hardware {:hardware-id "t2.micro"}
    :provider {:pallet-ec2 {:key-name "jem"               
                            :network-interfaces [{:device-index 0
                                                  :groups ["sg-0606b16e"]
                                                  :subnet-id "subnet-f929df91"
                                                  :associate-public-ip-address true
                                                  :delete-on-termination true}]}}))

(defn aws-provider 
  ([]
  (let 
    [aws-decrypted-credentials (get-in (pallet.configure/pallet-config) [:services :aws])]
    (compute/instantiate-provider
     :pallet-ec2
     :identity (get-in aws-decrypted-credentials [:account])
     :credential (get-in aws-decrypted-credentials [:secret])
     :endpoint "eu-central-1"
     :subnet-ids ["subnet-f929df91"])))
  ([key-id key-passphrase]
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
     :subnet-ids ["subnet-f929df91"]))))

(defn managed-ide-group []
  (api/group-spec
    "managed-ide-group"
    :extends [(config/with-config config) 
              init/with-init 
              managed-vm/with-dda-vm]
    :node-spec (aws-node-spec)
    :count 1))

(defn inspect-phase-plan []
  (session-tools/inspect-mock-server-spec
     managed-ide-group '(:settings :install)))
 
(defn do-sth 
  ([] 
    (api/converge
      (managed-ide-group)
      :compute (aws-provider)
      :phase '(:settings :init)
      :user (api/make-user "ubuntu")))
  ([key-id key-passphrase]
    (inspector/inspect-tree
      (api/converge
        (managed-ide-group)
        :compute (aws-provider key-id key-passphrase)
        :phase '(:settings :init :install :configure)
        :user (api/make-user "ubuntu"))))
)