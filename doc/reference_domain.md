The schema for the ide configuration is:

```clojure

(def Secret                         ; see dda-pallet-commons
  (either
    {:plain Str}                    ;   as plain text
    {:password-store-single Str}    ;   as password store key wo linebreaks & whitespaces
    {:password-store-record         ;   as password store entry containing login (record :login)
      {:path Str,                   ;      and password (no field or :password)
       :element (enum :password :login)}}
    {:password-store-multi Str}     ;   as password store key with linebreaks
    {:pallet-secret {:key-id Str,
                    :service-path [Keyword],
                    :record-element (enum :secret :account)}})

(def GitCredentials
  {(enum :gitblit :github)
   {:user s/Str
    (optional-key :password) Secret}})

(def User
  {:password Secret,
   :name Str,
   (optional-key :email) Str                    ; email for git config
   (optional-key :git-credentials)              ; credentials for git repositories
   git/GitCredentials
   (optional-key :desktop-wiki) [Str]           ; install zim desktop-wiki, Str to describe
                                                ; used autosync git repositories
   (optional-key :credentials) [Str]            ; install passwordstore or gopass, Str to describe
                                                ; used git repositories
   (optional-key :gpg) {:gpg-passphrase Secret
                        :gpg-public-key Secret
                        :gpg-private-key Secret}
   (optional-key :ssh) {:ssh-private-key Secret
                        :ssh-public-key Secret}})

(def Bookmarks                      ; see dda-managed-vm
  [{(optional-key :childs) [(recursive
                           (var
                            dda.pallet.dda-managed-vm.infra.browser/Folder))],
  :name Str,
  (optional-key :links) [[(one Str "url") (one Str "name")]]}])

(def Bookmarks                        ; see dda-managed-vm
  [{(optional-key :childs) [(recursive
                           (var
                            dda.pallet.dda-managed-vm.infra.browser/Folder))],
  :name Str,
  (optional-key :links) [[(one Str "url") (one Str "name")]]}])

(def DdaIdeDomainConfig
   {:target-type (s/enum :virtualbox :remote-aws :plain)
    :ide-platform (hash-set (s/enum :atom :idea))
    :user User
    (optional-key :bookmarks) Bookmarks
    (s/optional-key :git) git-domain/GitDomainConfig
    (s/optional-key :clojure) {(s/optional-key :lein-auth) [RepoAuth]}
    (s/optional-key :java) {}
    (s/optional-key :java-script) {}
    (s/optional-key :devops)
    {(s/optional-key :aws)
     {(s/optional-key :simple) {:id secret/Secret
                                :secret secret/Secret}}
     (s/optional-key :docker) {:bip s/Str}}}))
```
