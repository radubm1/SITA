> Project Description

SITA XML is a tool for extracting, transforming and loading data built
after an MVC architecture, intended to run in process mode (service,
daemon, etc.) at regular intervals on a capable machine with a server
role and which will interface the provider of data and
telecommunications services intended for air transport (SITA) with the
RAPID/SAP system in the sense of historical (or chronological) uploading
of data to a predetermined source(relational database), from where they
will be accessed by users in order to carry out synthetic reports using
predefined tools or customised in-house. This tool must respond to
internal internal reporting needs for a period of time equal to or
greater than 30 days (one month).

The SDCS Check-in Passenger Report in XML contains check-in passenger
data organized by carrier, flight number, and departure airport and
passenger name. The first carrier on the list is the Host Airline
followed by other Handled carriers. Passenger check-in status at the time of data capture and historical
updates leading to the current status are recorded in the report. The
report includes all passengers whether they were accepted or not.
Successfully check-in passengers are identified by a boarding number.
The XML format provides an interface facility for customers to
programmatically read and search the report.
The report is generated on the day the flight is purged out of the
system at the end of check-in flight life cycle. No recovery is
available once a flight is purged.

The internal model of the application consists of a collection of
classes whose instant-object are dynamically generated in the computer
memory by the SAX parser that recursively traverses a tree structure of
nodes and attributes (stored in a file . XML). In favor of adopting this
tool, it was the type of SGBD (Oracle) operated by our company and the
direct access (opposite to the sequential/iterative one) that it offers
to extract information from the attributes of the XML structure. This
model is validated referentially by manipulating class and instant
attributes of the whole type until the end of the parsing process (end
of file). The process of generating the Model is actually an intuitive
mapping (XML Schema -- a descriptive file with extension . DTD did not
come to me) of the tree structure in a relational structure.

The Controller component is an over-imposition of three levels, one
responsible for the actual extraction of data using SAXParser, the
second with the transformation of the data for the treatment of
exceptions (e.g. the appearance of null value in entire fields) and the
conversion of data (convertData() and convertNumber()) methods according
to the type specified in the relational model defined in the destination
database; the last level responsible for uploading data calls the
default DriverManager (jdbc:odbc) with the specification of the User DSN
data source configured and saved locally (e.g. SITADB -- Microsoft
Access Driver) and executes SQL instructions to insert set up (desirable
to create procedures stored on the server and call them during the
execution of the process, a measure that will reduce the time of
uploading data to the server).

The Viewer component is the default in SGBD Access or Oracle, both types
benefiting from reporting facilities, but can be explicitly defined for
online use using the XSLT language (for generating templates
corresponding to each type of view/statistic) or HTML for dynamic
reporting in the data source (using PHP on Server-side, of course).

Technical requirements for project implementation:

1.  storage space and access rights for a **data server** (MsSQLServer,
    Oracle, MySQL, samd.)

2.  **process server** with an internal memory capacity of at least 4GB,
    robust virtual memory paging mechanisms and virtual Java machine
    installed (JVM)
