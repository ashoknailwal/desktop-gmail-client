package database;

import java.sql.*;

/**
 * Created by Ashok on 4/14/2017.
 */
public class HelperValues {

    public static String loggedIn = "login";

    public static String getHelperValues(String name) throws SQLException, ClassNotFoundException {
        String res = null;
        Statement statement = h2DBConnection.getConnection().createStatement();
        String sql = "SELECT pairValue FROM helper WHERE name = '"+name+"'";
        ResultSet resultSet = statement.executeQuery(sql);
        if(resultSet.next())
            res = resultSet.getString("pairValue");
        resultSet.close();
        statement.close();
        return res;
    }

    public static void setHelperValues(String name , String value){

        try {
            PreparedStatement statement = h2DBConnection.getConnection().prepareStatement("INSERT INTO helper VALUES(?,?)");
            statement.setString(1,name);
            statement.setString(2,value);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void deleteHelperValue(String name){
        try {
            Statement s = h2DBConnection.getConnection().createStatement();
            s.executeUpdate("DELETE FROM helper WHERE name = '" + name + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
