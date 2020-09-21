package com.HolidayPlanner.HolidayPlanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.HolidayPlanner.HolidayPlanner.HolidayController;

@RunWith( SpringRunner.class )
@SpringBootTest
class HolidayPlannerApplicationTests
{

    @Autowired
    private HolidayController controller;


    @Test
    void contextLoads()
    {
        assertThat( controller ).isNotNull();
    }


    @Test
    public void testDates()
    {

        HolidayController holiday = new HolidayController();
        int resultWithSaturday = holiday.getDates( "2020-10-05", "2020-10-10", "FI" );
        int resultWithSunday = holiday.getDates( "2020-10-05", "2020-10-11", "FI" );
        int resultWithHolidays = holiday.getDates( "2021-01-01", "2021-01-11", "FI" );
        assertEquals( resultWithSaturday, 6 );
        assertEquals( resultWithSunday, 6 );
        assertEquals( resultWithHolidays, 7 );

    }

    @Test
    public void testTimeSpanLength()
    {

        assertThrows( IllegalArgumentException.class, new Executable()
        {

            @Override
            public void execute()
              throws Throwable
            {
                HolidayController holiday = new HolidayController();
                holiday.getDates( "2020-05-05", "2020-08-05", "FI" );
            }
        } );
    }

    @Test
    public void testStartDateIsNull()
    {
        assertThrows( IllegalArgumentException.class, new Executable()
        {

            @Override
            public void execute()
              throws Throwable
            {
                HolidayController holiday = new HolidayController();
                holiday.getDates( null, "2020-08-05", "FI" );
            }
        } );
    }

    @Test
    public void testEndDateIsNull()
    {

        assertThrows( IllegalArgumentException.class, new Executable()
        {

            @Override
            public void execute()
              throws Throwable
            {
                HolidayController holiday = new HolidayController();
                holiday.getDates( "2020-05-05", null, "FI" );
            }
        } );
    }

    @Test
    public void testCountryIsNull()
    {
        assertThrows( IllegalArgumentException.class, new Executable()
        {

            @Override
            public void execute()
              throws Throwable
            {
                HolidayController holiday = new HolidayController();
                holiday.getDates( "2020-05-05", "2020-05-07", null );
            }
        } );
    }


}
