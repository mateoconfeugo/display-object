A flexible way at building high performances graphical user interface composible components for responsive single page web appications

Seperate out the different aspects that are complected
* asynchronous event Handling
* callback delegation
* wiring application functionality to application behaviour
* HTML DOM manipulation
* CSS Style
* Content Managed data insertion into the dom
* Communication with the Server
* Commnincation with other Display Objects
* Interactive Development Environment
* Server and Client Workflows
* Isolating Display Object state and the manipulation of it
* Display Object Editors

Display Objects are declared according to a schema describing the combination of Clojure/ClojureScript code and media resources that will be consumed by
a constructor reifying the DisplayObject Protocol.

Display Objects can be created with the following environments
* clojure on the server side for the jvm
* clojurescript on the client for the browser
* clojurescript on the server side for nodejs

Display Objects are combination of:
* sample/current html
* samle/current css
* dom element channels
* channel handler functions
* enliven/enfocus transforms/snippet
* view model data channel - input stream
* html - output stream
* cms - managed content input stream
* target host element

Display objects are defind declaratively in a edn file.

During normal application runtime the principle channels used are the input data stream, the output data stream
and the dom event channels that display object has connected.

When a display object is being created or edited the css, html, cms are being used.  These channels when written to will change each the cms and html wondering if we can use a persistant queue or something like that to make undo and redo easy.  When the changes need to be saved/persisted on the server side,  The editor/layout manager can update the server which is updating edn files for the particular display object.

New display objects can be created from existing display objects.  Template html is simple inserted in the right spot and written to the html channel, same for css

Display objects get bound to their host element by the layout manager.
* When the data channel receives data via push, event, pull whatever
** Data read from channel land given to the snippet function which returns the new html
** the html is inserted into the target host element

layout manager consumes the html output channels and directs it to the correct set of target host elements
display objects sample html could be edited in place

the same could be said for the css

handler function editing on line can be accomplished by integrating kimera.

the layout manager has a map that connects the target host elements to the html output channels of the display objects.
The layout manager can be used to edit, add and remove display objects.
Operations that make sense.

(render-display-objects )
(route-html-to-host )
(route-html-to-hosts )
(add-display-object  )
(remove-display-object  )
(list-display-objects  )
(add-filter/transform )
(remove-filter/transform )
