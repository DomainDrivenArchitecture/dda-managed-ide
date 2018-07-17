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

(ns dda.pallet.dda-managed-ide.domain.git
  (:require
    [schema.core :as s]))

(s/defn
  ide-git-config
  [ide-config]
  (let [{:keys [user type]} ide-config
        {:keys [name email]
         :or {email (str name "@mydomain")}} user]
    {:os-user (keyword name)
     :user-email email
     :repos
     {:books
      ["https://github.com/DomainDrivenArchitecture/ddaArchitecture.git"]
      :password-store
      ["https://github.com/DomainDrivenArchitecture/password-store-for-teams.git"]
      :dda-pallet
      ["https://github.com/DomainDrivenArchitecture/dda-config-commons.git"
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
       "https://github.com/DomainDrivenArchitecture/dda-tomcat-crate.git"]}}))
