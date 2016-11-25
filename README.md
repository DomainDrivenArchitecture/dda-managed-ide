# dda-managed-ide
## without encryption
Define your credentials in your users home:

~/.pallet/config.clj
(defpallet
  :services
    {:aws
      {:account "-your aws account id-",
       :secret "-your unencrypted secret-"}
    }
)


(do-sth)

## with encryption
(do-sth "26E90AA6AA3ACBFE" "test1234")

## compatability
This crate is working with:
 * clojure 1.7
 * pallet 0.8
 * ubuntu 14.04
 
# License
Published under Apache2.0 License.
Copyright by meissa GmbH 2016
