<?php
error_reporting(E_ERROR | E_WARNING | E_PARSE | E_NOTICE);
global $Password;
class PasswordClass{
	protected $Options;
	public function __construct(){
		$Salt = mcrypt_create_iv(22, MCRYPT_DEV_URANDOM);
		$Salt = base64_encode($Salt);
		$Salt = str_replace('+', '.', $Salt);
		$this->Options=[
							'cost' => 9,
							'salt' => $Salt,
						];
		
	}
	function Info($HassedPassword,$Passtext){
		if (password_needs_rehash($HassedPassword, PASSWORD_BCRYPT, $this->Options)){
			$NewHassedPassword = password_hash($Passtext, PASSWORD_BCRYPT, $this->Options);
			$Data=	[
						'Status'=>'true',
						'NewHashedPassword'=>$NewHassedPassword,
					];
			return $Data;
		}else{	
			$Data=	[
						'Status'=>'false',
					];
			return $Data;
		}
	}
	function HashPassword($Passtext){
			return password_hash($Passtext, PASSWORD_BCRYPT, $this->Options);	
	}
	function __destruct() {}
}
$Password=new PasswordClass();
?>