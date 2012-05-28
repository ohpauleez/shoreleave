(defproject shoreleave "0.1.0-SNAPSHOT"
  :description "A smarter client-side with ClojureScript"
  :url "http://github.com/ohpauleez/shoreleave"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "See the notice in README.mkd or details in LICENSE.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [noir "1.3.0-beta3"]]
  :dev-dependencies [[vimclojure/server "2.3.1" :exclusions [org.clojure/clojure]] 
                     ;[lein-autodoc "0.9.0"]
                     ;[autodoc "0.9.0"]
                     [lein-marginalia "0.7.0-SNAPSHOT"]
                     [marginalia "0.7.0-SNAPSHOT"]
                     ;[cdt "1.2.6.2-SNAPSHOT"]
                     ;[lein-cdt "1.0.0"] ; use lein cdt to attach
                     ]
  :plugins  [[lein-cljsbuild "0.1.10"]]
  ;:cljsbuild {:source-path "src"
  ;            :compiler {:output-dir "resources/public/cljs/"
  ;                       :output-to "resources/public/cljs/bootstrap.js"
  ;                       :optimizations :simple
  ;                       :pretty-print true}}
  ;:repl-init-script "script/repl_init.clj"
  ;:main shoreleave.server
)

