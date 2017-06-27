package net.kimleo.dblite;

import net.kimleo.dblite.repl.Repl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DBTest {

    private DB postgres;

    @Before
    public void setUp() throws Exception {
        postgres = DB.connect("jdbc:postgresql://localhost/postgres", "postgres", "");
        postgres.withHandle(this::setUpTestData);
    }

    private void setUpTestData(Handle handle) {
        handle.execute("create table if not exists hello (world int)");
        handle.execute("delete from hello");
        for (int i = 0; i < 3; i++) {
            handle.execute(String.format("insert into hello (world) values (%d)", i + 1));
        }
    }

    @Test
    public void shouldSuccessfullyRunAQuery() throws Exception {
        postgres.withHandle(handle -> {
            Integer count = handle.one("select count(*) from hello", rs -> rs.getInt(1));

            assertThat(count, is(3));
        });
    }

    @Test
    public void shouldSuccessfullyRetrieveAllData() throws Exception {
        postgres.withHandle(handle -> {
            List<Integer> ints = handle.more("select * from hello", rs -> rs.getInt("world"));

            assertThat(ints, equalTo(Arrays.asList(1, 2, 3)));
        });
    }

    @After
    public void tearDown() throws Exception {
        postgres.withHandle(handle -> handle.execute("drop table if exists hello"));
    }
}