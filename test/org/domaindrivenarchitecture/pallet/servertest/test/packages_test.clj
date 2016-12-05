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


(ns org.domaindrivenarchitecture.pallet.servertest.test.packages-test
  (:require
    [clojure.test :refer :all]
    [pallet.build-actions :as build-actions]
    [pallet.actions :as actions]
    [org.domaindrivenarchitecture.pallet.servertest.test.packages :as sut]
    ))

(def packages-resource
  ["abiword						deinstall"
   "app-install-data				deinstall"
   "apt-xapian-index				deinstall"
   "gnumeric					deinstall"
   "libabiword-3.0:amd64				deinstall"])

(def named-packages-line
  {:package "abiword"
   :avail-operation "deinstall"})

(deftest test-parse
  (testing 
    "test parsing packages-output" 
      (is (= "abiword"
             (:package
               (first (sut/parse-packages packages-resource)))))
      ))

(deftest test-filter-installed
  (testing 
    "test for installed in one single line" 
      (is (sut/filter-installed-package "abiword" named-packages-line))
      (is (not (sut/filter-installed-package "gnumeric" named-packages-line)))
      ))

(deftest test-installed
  (testing 
    "test for installed packages" 
      (is (sut/installed? "gnumeric" packages-resource))
      ))