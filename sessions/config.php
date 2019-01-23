<?php
$Config;
class Database{
	protected $dbo;
	public function __construct(){
			$info['host_name'] = "localhost";
			$info['database'] = "manguo";
			$info['username'] = "root";
			$info['password'] = "";
			try{
				$this->dbo = new PDO("mysql:host=". $info['host_name'].";dbname=". $info['database']."", $info['username'], $info['password']);
				$this->dbo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_SILENT);
				}
				catch (PDOException $pe){
					die("Database Error." . $info['database'] . " :" . $pe->getMessage());
					}
	}
	function ConnectToDatabase(){
		return $this->dbo;
	}
	function __destruct() {		
	}
}
$Config=new Database();
?>