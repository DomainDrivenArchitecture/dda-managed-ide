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

(ns org.domaindrivenarchitecture.cm.config
  (:require
    [clojure.java.io :as io]
    [schema.core :as s]
    [org.domaindrivenarchitecture.pallet.crate.config.node :as node-record]
    [org.domaindrivenarchitecture.pallet.crate.user.ssh-key :as ssh-key-record]
    [org.domaindrivenarchitecture.pallet.crate.config :as config]    
    [org.domaindrivenarchitecture.pallet.convention.managed-ide :as convention]))

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

(def group-config
  (node-record/new-node 
    :host-name "my-vm" 
    :domain-name "meissa-gmbh.de"
    :additional-config (convention/ide-convention {:ide-user :vmuser
                                                   :vm-platform :aws
                                                   :dev-platform :clojure}))
  )

(def managed-ide-config
  {:ssh-keys ssh-keys
   :os-user os-user
   :group-specific-config {:managed-ide-group group-config}
   })
 