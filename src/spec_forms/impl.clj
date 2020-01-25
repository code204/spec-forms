(ns spec-forms.impl
  (:require [clojure.alpha.spec :as s]
            [clojure.alpha.spec.protocols :as protocols
             :refer [Spec]]
            [clojure.alpha.spec.gen :as gen]))

;; Modified from https://github.com/clojure/spec-alpha2/blob/7c708d063b6ea925fd406f87e08f508b7ed8c91d/src/main/clojure/clojure/alpha/spec/impl.clj#L51
(defn validator-impl
  [form message-form]
  (let [pred (s/resolve-fn form)
        message-fn (s/resolve-fn message-form)
        message-fn (if (fn? message-fn)
                     message-fn
                     (constantly message-fn))]
    (reify
      Spec
      (conform* [_ x settings-key settings]
        (if (pred x) x ::s/invalid))
      (unform* [_ x] x)
      (explain* [_ path via in x settings-key settings]
        (when-not (pred x)
          [{:path path :pred (#'s/unfn form) :val x :via via :in in
            :spec-forms/message (message-fn x)}]))
      (gen* [_ _ _ _]
        (gen/gen-for-pred pred))

;;TODO check with-gen*
      (with-gen* [_ gfn] (validator-impl form message-fn))
      (describe* [_] (#'s/unfn form)))))

(defmethod s/expand-spec 'spec-forms/validator
  [[_ & [form message-fn]]]
  {:clojure.spec/op 'spec-forms/validator
   :form form
   :message-fn message-fn})

(defmethod s/create-spec 'spec-forms/validator
  [{:keys [form message-fn]}]
  (validator-impl form message-fn))

