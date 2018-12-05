# jrss
JSON Raw Storage Stack

(WIP in its very first steps. Concept design so far.)

JRSS is an online repository for JSON data, enabling distributed applications to exchange information via HTTP in an extremely quick & dirty fashion.

JRSS simply stores text data chunks (foreseeably in JSON format, although this is not checked nor enforced) referred by an id. Storage is run in a stacked fashion: older values for a certain id are kept unless explicitly removed, so JRSS may be used for instant values as well as log storage.

## API overview ##
JRSS production API implements four basic operations: 

* **psh(stackid, data)**: _pushes_ new data into the stack referred by _stackid_. This id can be any string up to 1024 characters, but some sort of "package-like" scheme is advisable. The stack is created if it doesn't previously exist, and the data chunk is stored along with a timestamp.

* **pop(stackid)**: _pops_ last data from the stack referred by _stackid_, removing it from the stack. Regardless the actual data stored, the return value is always a JSON object; it'll be discused later.

* **get(stackid)**: serves the same purpose as _pop_, but data isn't removed from the stack.

* **rst(stackid)**: _resets_ the stack referred by _stackid_; i.e. destroy it.

These API calls are invoked as simple web URLs, e.g. http://your.server/jrss/push . Arguments (stackid and data) can be placed in the GET query string or submitted as POST form fields (the later being the preferred method for several reasons). As a result, the client can be anything that can request data from the web: JavaScript (AJAX), Java, Python, Perl, wget, curl... you name it.

The return value from JRSS basic operations is always a JSON object with three values:

* **"status"**: either "OK" or "ER"; self-explanatory.
* **"timestamp"**: the operation current date and time as a long integer (epoch). It's always the timestamp attached to the stored data, except for the rst operation.
* **"data"**: when status=="OK", its value is the stored data (except for the psh and rst operations, which would make no sense); if in "ER", its value is a two digit error code followed by a human readable description.

Foreseeably some advanced operations (e.g. stack rotation, reversion, dumping) will be needed, as well as a metadata query API. These aren't considered and this early phase of the project.

## Security ##
The security scheme for JRSS is called _No Object Needs Encryption_. That is: NONE.

Jokes apart, JRSS leaves security to other layers such as TCP-IP (IPsec, firewalling), SSL (HTTPS with mutual authentication) or even consumer application defined security (stackids and data encrypted prior to submission) when needed.

We're aware this can be a pain for large deployments, so a builtin client authentication mechanism may be implemented in the future. Data encryption will likely be not, anyway.

## Behind the scenes ##
JRSS is based on skel-app (https://github.com/a-zz/skel-app). The submitted data chunks are currently stored in a SQL database. skel-app provides data access abstraction, so any SQL database engine could be used; so far, only HSQLDB support is implemented, though.
