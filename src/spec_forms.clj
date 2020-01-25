(ns spec-forms
  (:require [clojure.alpha.spec :as s]
            [spec-forms.impl]))

(defmacro validator
  [& opts]
  `(s/resolve-spec
    '~(s/explicate
       (ns-name *ns*)
       `(validator ~@opts))))
