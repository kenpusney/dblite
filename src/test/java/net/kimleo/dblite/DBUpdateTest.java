package net.kimleo.dblite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DBUpdateTest {

    private DB postgres;

    @Before
    public void setUp() throws Exception {
        postgres = DB.connect("jdbc:postgresql://localhost/postgres", "postgres", "");
        postgres.withHandle(this::setUpTestData);
    }

    private void setUpTestData(Handle handle) {
        handle.execute("create table if not exists hello (id int, name varchar(20))");
        handle.execute("delete from hello");
    }

    @Test
    public void preparedStatementTest() {
        postgres.withHandle(handle -> {
            handle.prepared(1, "insert into hello (id, name) values (?, ?)", (stmt, n) -> {
                stmt.setInt(1, n);
                stmt.setString(2, "hello");
            });
        });

        postgres.withHandle(h -> {
            String name = h.one("select name from hello where id = 1", rs -> rs.getString(1));

            assertThat(name, is("hello"));
        });

        postgres.withHandle(h -> {
            String name = h.prepOne("select * from hello where id = ?", 1, ((stmt, n) -> {
                stmt.setInt(1, n);
            }), rs -> rs.getString(2));

            assertThat(name, is("hello"));
        });


    }


    @After
    public void tearDown() throws Exception {
        postgres.withHandle(handle -> handle.execute("drop table if exists hello"));
    }
}