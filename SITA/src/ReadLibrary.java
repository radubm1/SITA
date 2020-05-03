import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.io.*;
import java.lang.Object;
import java.text.Format;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.sql.*;

public class ReadLibrary extends DefaultHandler implements Runnable {
    
    File input;
    
    public static void main(String[] args) throws InterruptedException {
        if (args.length > 0) {
            long patience = 1000 * 60 * 60;
            for (int argc=0; argc < args.length; argc++) {
                ReadLibrary read = new ReadLibrary(args[argc]);
                long startTime = System.currentTimeMillis();
                Thread t = new Thread(read);
                t.start();
                while (t.isAlive()) {
                    // Wait maximum of 1 second
                    // for MessageLoop thread
                    // to finish.
                    t.join(1000);
                    if (((System.currentTimeMillis() - startTime) > patience)
                          && t.isAlive()) {
                        t.interrupt();
                        // Shouldn't be long now
                        // -- wait indefinitely
                        t.join();
                    }
                }
            }
        } else {
            //System.out.println("Usage: java ReadLibrary filename");collection.librml
            ReadLibrary read = new ReadLibrary("C:/RO01OCT13CKP.XML");
            new Thread(read).start();
        }
    }

    ReadLibrary(String libFile) {
        input = new File(libFile);
    }
    
    public void run(){
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        try {
            SAXParser sax = factory.newSAXParser();
            sax.parse(input, new AirlineHandler() );
        } catch (ParserConfigurationException pce) {
            System.out.println("Could not create that parser.");
            System.out.println(pce.getMessage());
        } catch (SAXException se) {
            System.out.println("Problem with the SAX parser.");
            System.out.println(se.getMessage());
        } catch (IOException ioe) {
            System.out.println("Error reading file.");
            System.out.println(ioe.getMessage());
        }
    }
}

class AirlineHandler extends DefaultHandler {
    static int READING_FLIGHT = 1;
    static int READING_CITY = 2;
    static int READING_PASSENGERS = 3;
    static int READING_PAXNAME = 4;
    static int READING_PASSENGER = 5;
    static int READING_OUTBOUND = 6;
    static int READING_INBOUND = 7;
    static int READING_SEATS = 8;
    static int READING_PAXITEMS = 9;
    static int READING_TKT = 10;
    static int READING_DOCS = 11;
    static int READING_PSM = 12;
    static int READING_BAGS = 13;
    static int READING_HISTORYITEM = 14;
    static int READING_NOTHING = 0;
    
    int currentActivity = READING_NOTHING;
    
    String data = "jdbc:odbc:SITASrv";
    
    Flight flight = new Flight();
    City city = new City();
    Passengers passengers = new Passengers();
    Paxname paxname = new Paxname();
    Passenger passenger = new Passenger();
    Inbound inbound = new Inbound();
    Outbound outbound = new Outbound();
    TKT tkt = new TKT();
      
    AirlineHandler() {
        super();
    }

    public void startElement(String uri, String localName,
        String qName, Attributes attributes) {
    
        if (qName.equals("Flight"))
            currentActivity = READING_FLIGHT;     
        else if (qName.equals("City"))
            currentActivity = READING_CITY;
        else if (qName.equals("Passengers"))
            currentActivity = READING_PASSENGERS;
        else if (qName.equals("Paxname"))
            currentActivity = READING_PAXNAME;
        else if (qName.equals("Passenger"))
            currentActivity = READING_PASSENGER;
        else if (qName.equals("Outbound"))
            currentActivity = READING_OUTBOUND;
        else if (qName.equals("Inbound"))
            currentActivity = READING_INBOUND;
        else if (qName.equals("Seats"))
            currentActivity = READING_SEATS;
        else if (qName.equals("Paxitems"))
            currentActivity = READING_PAXITEMS;
        else if (qName.equals("TKT"))
            currentActivity = READING_TKT;
        else if (qName.equals("DOCS"))
            currentActivity = READING_DOCS;
        else if (qName.equals("PSM"))
            currentActivity = READING_PSM;
        else if (qName.equals("Bags"))
            currentActivity = READING_BAGS;
        else if (qName.equals("HistoryItem"))
            currentActivity = READING_HISTORYITEM;
        else 
            currentActivity = READING_NOTHING;
        
         if (currentActivity == READING_FLIGHT) {
            flight.FlightNumber = attributes.getValue("FlightNumber");
            flight.FlightDate = attributes.getValue("FlightDate");
            int autoKey = 0;
            try {
                Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                Connection conn = DriverManager.getConnection(
                    data, "", "");                
                CallableStatement st = conn.prepareCall("INSERT INTO Flight ( FlightNumber, FlightDate ) VALUES(?,?);SELECT @@IDENTITY");
                st.setInt(1,convertNumber(flight.FlightNumber));
                st.setString(2, convertData(flight.FlightDate));
                st.execute();
                /*
                st.executeUpdate(
                "INSERT INTO Flight ( FlightNumber, FlightDate )" +
                " VALUES(" + convertNumber(flight.FlightNumber) + ", '" + convertData(flight.FlightDate) + "'));
                */
                ResultSet rs = st.getResultSet();
                if (rs != null){
                    rs.next();
                    autoKey=rs.getInt(1);
                }
                st.close();
                conn.close();
            } catch (SQLException s) {
                System.out.println("SQL Fly Error: " + s.toString() + " "
                    + s.getErrorCode() + " " + s.getSQLState());
            } catch (Exception e) {
                System.out.println("Error: " + e.toString()
                    + e.getMessage());
            }
           Flight.IdFlight = autoKey;
           //System.out.println(autoKey);
        }
         if (currentActivity == READING_CITY) {
            city.CityCode = attributes.getValue("CityCode");
            city.DepartureTime = attributes.getValue("DepartureTime");
            city.ArrivalTime = attributes.getValue("ArrivalTime");
            city.Equipment = attributes.getValue("Equipment");
            city.Configuration = attributes.getValue("Configuration");
            city.FkIdFlight=Flight.IdFlight;
            int autoKey = 0;
            try {
                Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                Connection conn = DriverManager.getConnection(
                    data, "", "");
                CallableStatement st = conn.prepareCall("INSERT INTO City (CityCode, DepartureTime, ArrivalTime, Equipment, Configuration, FkIdFlight ) VALUES(?,?,?,?,?,?);SELECT @@IDENTITY");
                st.setString(1, city.CityCode);
                st.setString(2, city.DepartureTime);
                st.setString(3, city.ArrivalTime);
                st.setString(4, city.Equipment);
                st.setString(5, city.Configuration);
                st.setInt(6, city.FkIdFlight);
                st.execute();
                
                /*
                st.executeUpdate(
                "INSERT INTO City (CityCode, DepartureTime, ArrivalTime, Equipment, Configuration, FkIdFlight )" +
                " VALUES('" + city.CityCode + "', '" + city.DepartureTime + "', '" + city.ArrivalTime + "', '" + city.Equipment + "', '" + city.Configuration + "', " + city.FkIdFlight + ")", Statement.RETURN_GENERATED_KEYS);
                */
                ResultSet rs = st.getResultSet();
                if (rs != null){
                    rs.next();
                    autoKey=rs.getInt(1);
                }
                st.close();
                conn.close();
            } catch (SQLException s) {
                System.out.println("SQL Cty Error: " + s.toString() + " "
                    + s.getErrorCode() + " " + s.getSQLState());
            } catch (Exception e) {
                System.out.println("Error: " + e.toString()
                    + e.getMessage());
            }         
            City.IdCity = autoKey;
        }
        if (currentActivity == READING_PASSENGERS) {
            passengers.FkIdCity=City.IdCity;
            int autoKey = 0;
            try {
                Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                Connection conn = DriverManager.getConnection(
                    data, "", "");
                CallableStatement st = conn.prepareCall("INSERT INTO Passengers ( FkIdCity ) VALUES(?);SELECT @@IDENTITY");
                st.setInt(1,passengers.FkIdCity);
                st.execute();
                /*
                st.executeUpdate(
                "INSERT INTO Passengers ( FkIdCity )" +
                " VALUES(" + passengers.FkIdCity + ")", Statement.RETURN_GENERATED_KEYS);
                */
                ResultSet rs = st.getResultSet();
                if (rs != null){
                    rs.next();
                    autoKey=rs.getInt(1);
                }
                st.close();
                conn.close();
            } catch (SQLException s) {
                System.out.println("SQL Pass Error: " + s.toString() + " "
                    + s.getErrorCode() + " " + s.getSQLState());
            } catch (Exception e) {
                System.out.println("Error: " + e.toString()
                    + e.getMessage());
            }
            Passengers.IdPassengers = autoKey;            
        }
        if (currentActivity == READING_PAXNAME) {
            paxname.Surname = attributes.getValue("Surname");
            paxname.Class = attributes.getValue("Class");
            paxname.GroupCode = attributes.getValue("GroupCode");
            paxname.Destination = attributes.getValue("Destination");
            paxname.FkIdPassengers=Passengers.IdPassengers;
            int autoKey = 0;
            try {
                Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                Connection conn = DriverManager.getConnection(
                    data, "", "");
                CallableStatement st = conn.prepareCall("INSERT INTO Paxname (Surname, Class, GroupCode, Destination, FkIdPassengers ) VALUES(?,?,?,?,?);SELECT @@IDENTITY");
                st.setString(1, paxname.Surname);
                st.setString(2, paxname.Class);
                st.setString(3, paxname.GroupCode);
                st.setString(4, paxname.Destination);
                st.setInt(5, paxname.FkIdPassengers);
                st.execute();
                /*
                st.executeUpdate(
                "INSERT INTO Paxname (Surname, Class, GroupCode, Destination, FkIdPassengers )" +
                " VALUES('" + paxname.Surname + "', '" + paxname.Class + "', '" + paxname.GroupCode + "', '" + paxname.Destination + "', " + paxname.FkIdPassengers + ")", Statement.RETURN_GENERATED_KEYS);
                */
                ResultSet rs = st.getResultSet();
                if (rs != null){
                    rs.next();
                    autoKey=rs.getInt(1);
                }
                st.close();
                conn.close();
            } catch (SQLException s) {
                System.out.println("SQL Pax Error: " + s.toString() + " "
                    + s.getErrorCode() + " " + s.getSQLState());
            } catch (Exception e) {
                System.out.println("Error: " + e.toString()
                    + e.getMessage());
            }
            Paxname.IdPaxname = autoKey;
        }
         if (currentActivity == READING_PASSENGER) {
            int autoKey = 0;
            passenger.FirstName = attributes.getValue("Firstname");
            passenger.Accepted = attributes.getValue("Accepted");
            passenger.BoardingNumber= attributes.getValue("BoardingNumber");
            passenger.XRES= attributes.getValue("XRES");
            passenger.FkIdPaxname=Paxname.IdPaxname;
            try {
                Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                Connection conn = DriverManager.getConnection(
                    data, "", "");
                CallableStatement st = conn.prepareCall("INSERT INTO Passenger ( FirstName, Accepted, BoardingNumber, XRES, FkIdPaxname ) VALUES(?,?,?,?,?);SELECT @@IDENTITY");
                st.setString(1, passenger.FirstName);
                st.setString(2, passenger.Accepted);
                st.setString(3, passenger.BoardingNumber);
                st.setString(4, passenger.XRES);
                st.setInt(5, passenger.FkIdPaxname);
                st.execute();
                /*
                st.executeUpdate(
                "INSERT INTO Passenger ( FirstName, Accepted, BoardingNumber, XRES, FkIdPaxname )" +
                " VALUES('" + passenger.FirstName + "', '" + passenger.Accepted + "', " + convertNumber(passenger.BoardingNumber) + ", '" + passenger.XRES + "', " + passenger.FkIdPaxname + ")", Statement.RETURN_GENERATED_KEYS);
                */
                ResultSet rs = st.getResultSet();
                if (rs != null){
                    rs.next();
                    autoKey=rs.getInt(1);
                }
                st.close();
                conn.close();
            } catch (SQLException s) {
                System.out.println("SQL Pas Error: " + s.toString() + " "
                    + s.getErrorCode() + " " + s.getSQLState());
            } catch (Exception e) {
                System.out.println("Error: " + e.toString()
                    + e.getMessage());
            }
            Passenger.IdPassenger = autoKey;
        }
        if (currentActivity == READING_INBOUND) {
            int autoKey = 0;
            inbound.Airline = attributes.getValue("Airline");
            inbound.FlightNumber = attributes.getValue("FlightNumber");
            inbound.FlightDate = attributes.getValue("FlightDate");
            inbound.Class = attributes.getValue("Class");
            inbound.Origin = attributes.getValue("Origin");
            inbound.Accepted = attributes.getValue("Accepted");
            inbound.FkIdInboundPassenger = Passenger.IdPassenger;
            try {
                Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                Connection conn = DriverManager.getConnection(
                    data, "", "");
                CallableStatement st = conn.prepareCall("INSERT INTO Inbound (Airline, FlightNumber, FlightDate, Class, Origin, Accepted, FkIdInboundPassenger ) VALUES(?,?,?,?,?,?,?);SELECT @@IDENTITY");
                st.setString(1, inbound.Airline);
                st.setInt(2, convertNumber(inbound.FlightNumber));
                st.setString(3, convertData(inbound.FlightDate));
                st.setString(4, inbound.Class);
                st.setString(5, inbound.Origin);
                st.setString(6, inbound.Accepted);
                st.setInt(7, inbound.FkIdInboundPassenger);
                st.execute();
                /*
                st.executeUpdate(
                "INSERT INTO Inbound (Airline, FlightNumber, FlightDate, Class, Origin, Accepted, FkIdInboundPassenger )" +
                " VALUES('" + inbound.Airline + "', " + convertNumber(inbound.FlightNumber) + ", '" + convertData(inbound.FlightDate) + "', '" + inbound.Class + "', '" + inbound.Origin + "', '" + inbound.Accepted + "', " + inbound.FkIdInboundPassenger + ")", Statement.RETURN_GENERATED_KEYS);
                */
                ResultSet rs = st.getResultSet();
                if (rs != null){
                    rs.next();
                    autoKey=rs.getInt(1);
                }
                st.close();
                conn.close();
            } catch (SQLException s) {
                System.out.println("SQL In Error: " + s.toString() + " "
                    + s.getErrorCode() + " " + s.getSQLState());
            } catch (Exception e) {
                System.out.println("Error: " + e.toString()
                    + e.getMessage());
            }
            Inbound.IdInbound = autoKey;
        }
        if (currentActivity == READING_OUTBOUND) {
            int autoKey = 0;
            outbound.Airline = attributes.getValue("Airline");
            outbound.FlightNumber = attributes.getValue("FlightNumber");
            outbound.FlightDate = attributes.getValue("FlightDate");
            outbound.Class = attributes.getValue("Class");
            outbound.Destination = attributes.getValue("Destination");
            outbound.FkIdOutboundPassenger = Passenger.IdPassenger;
            try {
                Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                Connection conn = DriverManager.getConnection(
                    data, "", "");
                CallableStatement st = conn.prepareCall("INSERT INTO Outbound ( Airline, FlightNumber, FlightDate, Class, Destination, FkIdOutboundPassenger ) VALUES(?,?,?,?,?,?);SELECT @@IDENTITY");
                st.setString(1, outbound.Airline);
                st.setInt(2, convertNumber(outbound.FlightNumber));
                st.setString(3, convertData(outbound.FlightDate));
                st.setString(4, outbound.Class);
                st.setString(5, outbound.Destination);
                st.setInt(6, outbound.FkIdOutboundPassenger);
                st.execute();
                /*
                st.executeUpdate(
                "INSERT INTO Outbound ( Airline, FlightNumber, FlightDate, Class, Destination, FkIdOutboundPassenger )" +
                " VALUES('" + outbound.Airline + "', " + convertNumber(outbound.FlightNumber) + ", '" + convertData(outbound.FlightDate) + "', '" + outbound.Class + "', '" + outbound.Destination + "', " + outbound.FkIdOutboundPassenger + ")", Statement.RETURN_GENERATED_KEYS);
                */
                ResultSet rs = st.getResultSet();
                if (rs != null){
                    rs.next();
                    autoKey=rs.getInt(1);
                }
                st.close();
                conn.close();
            } catch (SQLException s) {
                System.out.println("SQL Out Error: " + s.toString() + " "
                    + s.getErrorCode() + " " + s.getSQLState());
            } catch (Exception e) {
                System.out.println("Error: " + e.toString()
                    + e.getMessage());
            }
            Outbound.IdOutbound = autoKey;
            
        }
         if (currentActivity == READING_TKT) {
            int autoKey = 0;
            tkt.Type = attributes.getValue("Type");
            tkt.Airline = attributes.getValue("Airline");
            tkt.CouponNumber = attributes.getValue("CouponNumber");
            tkt.TicketNumber = attributes.getValue("TicketNumber");
            tkt.FkIdTKTPassenger = Passenger.IdPassenger;
            try {
                Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                Connection conn = DriverManager.getConnection(
                    data, "", "");
                CallableStatement st = conn.prepareCall("INSERT INTO TKT ( Type, Airline, TicketNumber, CouponNumber, FkIdTKTPassenger ) VALUES(?,?,?,?,?);SELECT @@IDENTITY");
                st.setString(1,tkt.Type);
                st.setString(2,tkt.Airline);
                st.setString(3,tkt.TicketNumber);
                st.setInt(4,convertNumber(tkt.CouponNumber));
                st.setInt(5,tkt.FkIdTKTPassenger);
                st.execute();
                /*
                st.executeUpdate(
                //System.out.println(
                "INSERT INTO TKT ( Type, Airline, TicketNumber, CouponNumber, FkIdTKTPassenger )" +
                " VALUES('" + tkt.Type + "', '" + tkt.Airline + "', '" + tkt.TicketNumber + "', " + convertNumber(tkt.CouponNumber) + ", " + tkt.FkIdTKTPassenger + ")", Statement.RETURN_GENERATED_KEYS);
                */
                ResultSet rs = st.getResultSet();
                if (rs != null){
                    rs.next();
                    autoKey=rs.getInt(1);
                }
                st.close();
                conn.close();
            } catch (SQLException s) {
                System.out.println("SQL TKT Error: " + s.toString() + " "
                    + s.getErrorCode() + " " + s.getSQLState());
            } catch (Exception e) {
                System.out.println("Error: " + e.toString()
                    + e.getMessage());
            }
            TKT.IdTKT = autoKey; 
        }
    }

   public void endElement(String uri, String localName, String qName) {
       
       if (qName.equals("TKT")) {
           tkt = new TKT();
       }
       
       if (qName.equals("Outbound")) {
           outbound = new Outbound();
       }
       
       if (qName.equals("Inbound")) {
           inbound = new Inbound();
       }
       
       if (qName.equals("Passenger")) {
           passenger = new Passenger();
       }
       
        if (qName.equals("Paxname")) {
            paxname= new Paxname();
        }
 
       if (qName.equals("Passengers")) {
           passengers = new Passengers();
       }
       
       if (qName.equals("City")) {
           city = new City();
       }
       
       if (qName.equals("Flight")) {
           flight = new Flight();
       }       
    } 
   public String convertData(String in)
   {
    Date date=null;        
    SimpleDateFormat informatter = new SimpleDateFormat("ddMMMyy");
    SimpleDateFormat outformatter = new SimpleDateFormat("MM/dd/yy");

    try {
        date = informatter.parse(in);
        //System.out.println(date);
    } catch (ParseException e) {
        e.printStackTrace();
    }
    return outformatter.format(date);
   }
   
   public int convertNumber(String in)
   {
    if (in == null) {
      return 0;
    }
    else return Integer.parseInt(in.trim());
   }
}

class Flight {
    static int IdFlight=0;
    String FlightNumber;
    String FlightDate;
}

class City{
    static int IdCity=0;
    String CityCode;
    String DepartureTime;
    String ArrivalTime;
    String Equipment;
    String Configuration;
    int FkIdFlight;
}

class Passengers {
    static int IdPassengers=0;
    int FkIdCity;
}

class Paxname {
    static int IdPaxname=0;
    String Surname;
    String Class;
    String GroupCode;
    String Destination;
    int FkIdPassengers;
}

class Passenger {
    static int IdPassenger=0;
    String FirstName;
    String Accepted;
    String BoardingNumber;
    String XRES;
    int FkIdPaxname;
}

class Inbound {
    static int IdInbound;
    String Airline;
    String FlightNumber;
    String FlightDate;
    String Class;
    String Origin;
    String Accepted;
    int FkIdInboundPassenger;
}

class Outbound {
    static int IdOutbound;
    String Airline;
    String FlightNumber;
    String FlightDate;
    String Class;
    String Destination;
    int FkIdOutboundPassenger;
}

class Seat {
    static int IdSeat=0;
    String Seat;
    int FkIdSeatsPassenger;
}

class PaxItems {
    static int IdPaxItems=0;
    String Code;
    String Details;
    int FkIdPaxPassenger;
}

class TKT{
    static int IdTKT=0;
    String Type;
    String Airline;
    String TicketNumber;
    String CouponNumber;
    int FkIdTKTPassenger;
}


