# jrss
JSON Raw Storage Stack

JRSS is an online repository for JSON data, enabling distributed applications to exchange information via HTTP in an extremely quick & dirty fashion.

JRSS simply stores text data chunks (foreseeably in JSON format, although this is not checked nor enforced) referred by an id. Storage is run in a stacked fashion: older values for a certain id are kept unless explicitly removed, so JRSS may be used for instant or historic values, log storage and -well- a proper stack functionality.

## API overview ##
JRSS production API implements the two basic operations you may expect from a stack **push** and **pop**. JRSS can handle multiple stacks, referred by diferent _stackid's_. Each stack entry is stored along with a timestamp.

For convenience, two additional operations are implemented: **get** (same as pop, but the data retrieved isn't deleted from the stack); and **reset**, to destroy the stack.

These API calls are invoked as simple web URLs, e.g. http://your.server/jrss/psh. Arguments (stackid and data) can be placed in the GET query string or submitted as POST form fields (the later being the preferred method for several reasons). As a result, the client can be anything that can request data from the web: JavaScript (AJAX), Java, Python, Perl, wget, curl... you name it.

The return value from JRSS basic operations is always a JSON object with three values:

* **"status"**: either "OK" or "ER"; self-explanatory.
* **"timestamp"**: the operation current date and time as a long integer (epoch).
* **"data"**: the stored data or error message.

For a complete description of the API please see: doc/jrss.txt

Several other advanced operations (stack rotation, reversion, dumping), as well as a metadata API, are expected to come anytime. Stay tuned.

## Security ##
The security scheme for JRSS is called _No Object Needs Encryption_. That is: NONE.

Jokes apart, JRSS leaves security to other layers such as TCP-IP (IPsec, firewalling), SSL (HTTPS with mutual authentication) or even consumer-application-defined security (stackids and data encrypted prior to submission) when needed.

We're aware this can be a pain for large deployments, so a builtin client authentication mechanism may be implemented in the future. Data encryption will likely be not, anyway.

## Behind the scenes ##
JRSS is based on skel-app ([https://github.com/a-zz/skel-app]). The submitted data chunks are currently stored in a SQL database. skel-app provides data access abstraction, so any SQL database engine could be used; so far, only HSQLDB support is implemented, though.

## Project status ##
As said above (see section _API overview_) JRSS lacks several advanced operation and a metadata API. Functionality implemented still need production-grade testing. It should work fairly well for personal projects, though.
