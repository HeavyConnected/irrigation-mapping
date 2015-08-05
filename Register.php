<? php
  $con=mysqli_connect("localhost", "my_user", "my_password", "my_db");

  $name = $_POST["name"];
  $username = $_POST["username"];
  $password = $_POST["password"];

  $statement =  mysqli_prepare($con, "INSERT INTO User (name, username, password) VALUES(?, ?, ?)");
  //sss because all of them are strings.
  mysqli_stmt_bind_param($statement, "sss", $name, $username, $password);
  mysqli_stmt_execute($statement);
  mysqli_stmt_close($statement);

  mysqli_close($con);
?>
