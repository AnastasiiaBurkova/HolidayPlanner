package com.HolidayPlanner.HolidayPlanner;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping( value = "/api" )
public class HolidayController
{
    /**
     * Path to the countries directory.
     */
    public String selectedCountry = "/Users/aburkova/Code/Task/HolidayPlanner/src/main/resources/static/countries/";

    /**
     * Json value of the holiday array.
     */
    public String countryHolidays = "countryHolidays";
    /**
     * Time format.
     */
    public String timeFormat = "yyyy-MM-dd";

    /**
     * Fetches the time span and the country, estimates the number of holidays and
     * the number of work days and returns the final number of days the person should take
     * to be on holidays during the given time period.
     *
     * @param startDate start date of the time span.
     * @param endDate   end date of the time span.
     * @param country   selected country.
     * @return a number of holiday days the person should take.
     */
    @RequestMapping( value = "/dates", method = RequestMethod.POST )
    public int getDates( @RequestParam( value = "startDate" ) String startDate,
                         @RequestParam( value = "endDate" ) String endDate,
                         @RequestParam( value = "country" ) String country )
    {

        if( startDate == null || endDate == null || country == null )
        {
            throw new IllegalArgumentException( "The required parameters for the start process are missing! Start date: " + startDate
              + ". End date: " + endDate + ". Country: " + country );
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern( timeFormat );
        LocalDate firstDate = LocalDate.parse( startDate, formatter );
        LocalDate secondDate = LocalDate.parse( endDate, formatter );

        if( daysInTimeSpan( firstDate, secondDate ).size() > 50 )
        {
            throw new IllegalArgumentException( "The time span is too big. Shouldn't exceed 50 days." );
        }

        List<LocalDate> allDays = daysInTimeSpan( firstDate, secondDate );
        List<LocalDate> holidays = parseFileData( selectedCountry + country + ".json", countryHolidays );

        List<LocalDate> common = new ArrayList<>( allDays );
        common.retainAll( holidays );

        List<Integer> workDays = new ArrayList<>();
        for( int i = 0; i < allDays.size(); i++ )
        {
            Calendar date = Calendar.getInstance();
            date.setTime( Date.from( allDays.get( i ).atStartOfDay( ZoneId.systemDefault() ).toInstant() ) );
            int dayOfWeek = date.get( Calendar.DAY_OF_WEEK );

            if( dayOfWeek != 1 )
            {
                workDays.add( dayOfWeek );
            }
        }

        int numOfWorkDays = workDays.size();
        int numOfHolidays = common.size();

        return numOfWorkDays - numOfHolidays;
    }

    /**
     * Looks for the number of dates in the time span and
     * returns a list of all dates.
     *
     * @param startDate start date of the time span.
     * @param endDate   end date of the time span.
     * @return a list of dates in the time span.
     */
    public List<LocalDate> daysInTimeSpan( LocalDate startDate, LocalDate endDate )
    {
        long numOfDays = ChronoUnit.DAYS.between( startDate, endDate ) + 1;

        return IntStream.iterate( 0, i -> i + 1 )
          .limit( numOfDays )
          .mapToObj( i -> startDate.plusDays( i ) )
          .collect( Collectors.toList() );
    }

    /**
     * Reads json from the file, iterates through the json list and
     * returns an array list of holidays' dates.
     *
     * @param filePath path to country dir.
     * @param value    value in the json file.
     * @return list of country's holidays' dates.
     */
    public List<LocalDate> parseFileData( String filePath, String value )
    {
        JSONParser parser = new JSONParser();
        List<LocalDate> holidays = new ArrayList<>();
        try
        {
            Object obj = parser.parse( new FileReader( filePath ) );
            JSONObject jsonObject = (JSONObject)obj;
            JSONArray list = (JSONArray)jsonObject.get( value );
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern( timeFormat );
            Iterator<String> iterator = list.iterator();

            while( iterator.hasNext() )
            {
                holidays.add( LocalDate.parse( iterator.next(), formatter ) );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return holidays;
    }

}