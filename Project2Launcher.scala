package module2project

import java.io.{File, PrintWriter}

import module2project.model.{Calendar, EnrichedTrip, Route, Trip, TripRoute}

import scala.io.Source

object Project2Launcher extends App{
  val fileSource = Source.fromFile("src/main/scala/module2project/iofiles/input/routes.txt")
  val routes: List[Route] = fileSource
    .getLines.drop(1)
    .toList
    .map(_.split(",", -1))
    .map(cols => Route(cols(0), cols(1), cols(2), cols(3), cols(4), cols(5), cols(6), cols(7)))
  fileSource.close

  val fileSource2 = Source.fromFile("src/main/scala/module2project/iofiles/input/trips.txt")
  val trips: List[Trip] = fileSource2
    .getLines.drop(1)
    .toList
    .map(_.split(",", -1))
    .map(cols => Trip(cols(0), cols(1), cols(2), cols(3), cols(4), cols(5), cols(6), cols(7), cols(8)))
  fileSource2.close()

  val fileSource1 = Source.fromFile("src/main/scala/module2project/iofiles/input/calendar.txt")
  val calendars: List[Calendar] = fileSource1
    .getLines.drop(1)
    .toList
    .map(_.split(",", -1))
    .map(cols => Calendar(cols(0), cols(1), cols(2), cols(3), cols(4), cols(5), cols(6), cols(7), cols(8), cols(9)))
  val calendarLookup: CalendarLookup = new CalendarLookup(calendars)
  fileSource1.close()


  val routesTuple: Map[String, Route] = routes.map(route => route.route_id -> route ).toMap
  val tripRoute = trips.filter(trip => routesTuple.contains(trip.route_id)).map(trip => TripRoute(trip, routesTuple(trip.route_id)))

  val enrichedTrip = tripRoute.map(tripRoute => {new EnrichedTrip(tripRoute, new CalendarLookup(calendars).lookup(tripRoute.trips.service_id))})

  val writer = new PrintWriter(new File("src/main/scala/module2project/iofiles/output//enrichedTrip.csv"))
  writer.write("Trip Id, Route Id, Service Id, Trip Head Sign, Route Name, Monday\n")
  enrichedTrip.foreach(enrichedTrip => writer.write(
    enrichedTrip.tripRoute.trips.trip_id +
      "," + (if (enrichedTrip.tripRoute.routes != null) enrichedTrip.tripRoute.routes.route_id else "NA") +
      "," + (enrichedTrip.tripRoute.trips.service_id) +
      "," + (enrichedTrip.tripRoute.trips.trip_headsign) +
      "," + (if (enrichedTrip.tripRoute.routes != null) enrichedTrip.tripRoute.routes.route_long_name else "NA") +
      "," + (if (enrichedTrip.calendar != null) { if (enrichedTrip.calendar.monday.equals("1")) "Yes" else "No"} else "NA") +
      "\n"))
  writer.close()
}
