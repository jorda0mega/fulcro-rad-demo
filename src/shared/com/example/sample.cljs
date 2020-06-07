(ns com.example.sample
  (:require
    [com.fulcrologic.guardrails.static.checker :refer [>>defn]]
    [cljs.spec.alpha :as s]))

(s/def :person/name string?)
(s/def :person/age int?)
(s/def ::int-or-string (s/or :i int? :s string?))
(s/def ::person (s/keys :req [:person/name :person/age]))
(s/def ::narrow-person (s/keys
                         :req [:person/name]
                         :opt [:person/age]))

(>>defn new-person
  [name age]
  [string? int? => ::person]
  {:person/name name :person/age age})

(>>defn bump-age
  [person]
  [::person => ::person]
  (update person :person/age inc))

(>>defn add-salutation
  [p]
  ;; TODO: type variables. On parameter specify minimum requirements, duplicating to output indicates that the
  ;; shape of the real data wasn't "harmed" and it should still be considered the same kind of thing it was going in
  ^{:a (s/keys :req [:person/name])} [:a => :a]
  (update p :person/name (fn [n] (str "Hi, " n))))

(>>defn manipulate-person
  []
  [=> ::person]
  (let [p  (new-person "Bob" 33)
        p2 (add-salutation p)
        p3 (bump-age p2)]
    p3))
