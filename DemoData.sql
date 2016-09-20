-- MySQL dump 10.13  Distrib 5.7.15, for Win32 (AMD64)
--
-- Host: localhost    Database: quiz
-- ------------------------------------------------------
-- Server version	5.7.15-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activitylog`
--

DROP TABLE IF EXISTS `activitylog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activitylog` (
  `username` varchar(50) DEFAULT NULL,
  `activity` varchar(250) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activitylog`
--

LOCK TABLES `activitylog` WRITE;
/*!40000 ALTER TABLE `activitylog` DISABLE KEYS */;
INSERT INTO `activitylog` VALUES ('teach','Teacher application submitted','2016-09-20 03:16:05'),('teach','User logged in','2016-09-20 03:16:25'),('teach','User logged out','2016-09-20 03:21:11'),('student','student registered','2016-09-20 03:21:26'),('student','User logged in','2016-09-20 03:21:29'),('student','User Started Test','2016-09-20 03:21:34'),('student','Student issued cheat warnings.','2016-09-20 03:22:12'),('student','Student issued cheat warnings.','2016-09-20 03:22:15'),('student','Student issued cheat warnings.','2016-09-20 03:22:24'),('student','Student issued cheat warnings.','2016-09-20 03:22:27'),('student','Student issued cheat warnings.','2016-09-20 03:22:30'),('student','User Finished Test','2016-09-20 03:22:34'),('student','User logged out','2016-09-20 03:22:42');
/*!40000 ALTER TABLE `activitylog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `errorlog`
--

DROP TABLE IF EXISTS `errorlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `errorlog` (
  `username` varchar(50) DEFAULT NULL,
  `particulars` varchar(2500) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `errorlog`
--

LOCK TABLES `errorlog` WRITE;
/*!40000 ALTER TABLE `errorlog` DISABLE KEYS */;
INSERT INTO `errorlog` VALUES ('teach','User generated','2016-09-20 03:18:37'),('teach','User generated','2016-09-20 03:20:05'),('teach','User generated','2016-09-20 03:20:12');
/*!40000 ALTER TABLE `errorlog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_auth`
--

DROP TABLE IF EXISTS `student_auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `student_auth` (
  `name` varchar(50) NOT NULL,
  `password` varchar(512) DEFAULT NULL,
  `onlinestatus` int(1) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_auth`
--

LOCK TABLES `student_auth` WRITE;
/*!40000 ALTER TABLE `student_auth` DISABLE KEYS */;
INSERT INTO `student_auth` VALUES ('student','1661548902539077689835780616605042428580731493960493799641588268547596371700',0);
/*!40000 ALTER TABLE `student_auth` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `studenthistorydatabase_student`
--

DROP TABLE IF EXISTS `studenthistorydatabase_student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `studenthistorydatabase_student` (
  `testid` varchar(50) DEFAULT NULL,
  `marksearned` int(5) DEFAULT NULL,
  `aborted` int(1) DEFAULT NULL,
  `cheatwarnings` int(2) DEFAULT NULL,
  `datetaken` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `studenthistorydatabase_student`
--

LOCK TABLES `studenthistorydatabase_student` WRITE;
/*!40000 ALTER TABLE `studenthistorydatabase_student` DISABLE KEYS */;
INSERT INTO `studenthistorydatabase_student` VALUES ('ip_1',0,0,5,'2016-09-20 03:22:34');
/*!40000 ALTER TABLE `studenthistorydatabase_student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `systemsettings`
--

DROP TABLE IF EXISTS `systemsettings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `systemsettings` (
  `identifier` varchar(50) DEFAULT NULL,
  `data` varchar(5000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `systemsettings`
--

LOCK TABLES `systemsettings` WRITE;
/*!40000 ALTER TABLE `systemsettings` DISABLE KEYS */;
INSERT INTO `systemsettings` VALUES ('totalcheatseconds','25'),('totalallowedwarnings','5'),('wakeupseconds','300'),('flashwarningseconds','60'),('loglocation','D:/Online Test/Error Log.txt'),('reslocation','D:/Online Test/Resources/');
/*!40000 ALTER TABLE `systemsettings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_auth`
--

DROP TABLE IF EXISTS `teacher_auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `teacher_auth` (
  `name` varchar(50) NOT NULL,
  `password` varchar(512) DEFAULT NULL,
  `subject` varchar(30) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  `onlinestatus` int(1) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_auth`
--

LOCK TABLES `teacher_auth` WRITE;
/*!40000 ALTER TABLE `teacher_auth` DISABLE KEYS */;
INSERT INTO `teacher_auth` VALUES ('teach','1661548902539077689835780616605042428580731493960493799641588268547596371700','Informatics Practices',1,0);
/*!40000 ALTER TABLE `teacher_auth` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `testlist`
--

DROP TABLE IF EXISTS `testlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `testlist` (
  `testid` varchar(50) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  `subject` varchar(50) DEFAULT NULL,
  `points` int(2) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  `datecreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `seconds` int(11) DEFAULT NULL,
  PRIMARY KEY (`testid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `testlist`
--

LOCK TABLES `testlist` WRITE;
/*!40000 ALTER TABLE `testlist` DISABLE KEYS */;
INSERT INTO `testlist` VALUES ('ip_1','teach','Test question-1','Informatics Practices',5,1,'2016-09-20 03:17:22',1800);
/*!40000 ALTER TABLE `testlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `testquestions_ip_1`
--

DROP TABLE IF EXISTS `testquestions_ip_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `testquestions_ip_1` (
  `sno` int(11) DEFAULT NULL,
  `question` varchar(2500) DEFAULT NULL,
  `answer` varchar(5) DEFAULT NULL,
  `imagesource` varchar(2500) DEFAULT NULL,
  `reserve` int(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `testquestions_ip_1`
--

LOCK TABLES `testquestions_ip_1` WRITE;
/*!40000 ALTER TABLE `testquestions_ip_1` DISABLE KEYS */;
INSERT INTO `testquestions_ip_1` VALUES (1,'Lorem ipsum dolor sit amet, dolore perfecto vix te, est graecis efficiendi id. Illum melius nusquam cum no, his latine conceptam ne! An pri mutat movet. Dicat timeam mei id, qui no falli dictas. Vix ex oblique abhorreant cotidieque.','a','D:/Online Test/Resources/ip_1/Question_1.jpg',0),(2,' Perfecto disputationi ut vel, vim movet dicit decore ne. Sea facete pertinax corrumpit at, no nec dicta menandri expetendis, sonet democritum vix at. Meis persius ne mel, ut eam ridens definiebas, nec cu vidit choro libris. Ad duis platonem eos! Nibh labitur ut vix, malis nihil officiis sea ei, ex liber dolor tincidunt mel.','b','',0),(3,' Nec ne magna quando, ad eum cibo everti habemus? Suas volumus singulis vim et. An perfecto appellantur nec, mel an aperiam nusquam facilisis. Id nonumes voluptatum argumentum nec. Quo etiam honestatis dissentiet an?','c','',0),(4,' Oratio percipit cu has, ad cibo malis epicurei vix? At vidit antiopam cum, mei nulla facilis eu. Alia ludus iriure est te, duis possim nostrum pro ad? Eu simul denique reprimique vim, molestie rationibus persequeris cu eos! Duo te feugiat evertitur scribentur.','d','',0),(5,' Eam et quando malorum, usu feugiat tacimates ad? Et mel graece partiendo adversarium, an explicari referrentur has, ius et utinam utroque officiis. Accusamus tincidunt assueverit id sed, pri an sint sensibus. Eu electram sapientem intellegat sea, pro te brute persius blandit?','a','',0),(6,' Mea ullum minim nusquam ut, nec putent voluptaria cu. Nostrum nominavi id ius, mei nominati volutpat in. Ad esse audire lobortis est, no vix mazim quaestio. Tota eirmod rationibus his at, doctus iudicabit tincidunt vis no! Duis audire an ius?','b','',0),(7,' Mel autem dicunt ei. Forensibus elaboraret sed cu, mel assueverit dissentias eu. Ius falli graeci praesent ad! Altera corrumpit voluptaria his at, id vim tamquam nominavi elaboraret, cum no vitae posidonium? Vix vidit prima reformidans at, usu cu fuisset fastidii delicatissimi, cu ius summo persecuti. Ea mei fugit dignissim.','c','',0),(8,' Et aliquid admodum vim, illum exerci primis no nec, ne essent scaevola est? Erant eloquentiam dissentiunt qui at, ei iusto scripta consequuntur his. In aliquip regione his, sit nemore conclusionemque an. Eos ea laudem postea blandit, dicunt similique signiferumque eam ut.','d','',0),(9,' Habeo rebum exerci nec et, cum inani electram consulatu te, eu sit reformidans definitiones! Copiosae suavitate in vim! Ea quem tation sensibus ius. Sea id alii velit feugait, mei populo reprimique constituam ei?','a','',0),(10,' Id vide similique sit, no unum consul disputationi cum, populo quodsi pri eu! Soleat eirmod mei ei. Sea an munere perpetua. In vel nobis civibus efficiantur. In euismod democritum expetendis qui, eu mel posse petentium.','b','',0),(11,' Case omnis sit et, te cum hinc officiis? In liber iusto omnesque eum, sumo accumsan sed no, ei vel adipisci consetetur. Et latine viderer qui, choro integre aliquid has in! Sed ad nihil ancillae, purto urbanitas ex has. Ei eam saepe gubergren complectitur, utroque alienum no eam. Nec eu brute clita nemore, eum ei argumentum sadipscing, mei velit melius efficiendi no.','c','',0),(12,' No mea probatus gloriatur, at impetus sententiae quaerendum pri, partem vidisse vituperatoribus ad vix. In ius ignota nostro cetero, ea quo virtute docendi sententiae, usu cu esse docendi. Sea ne dicat petentium, usu ea alienum sensibus dissentiunt, ad eos veri inani. Vis suas doctus facilis ne!','d','',0),(13,' Ornatus inermis temporibus mel ea, ne sed tincidunt consectetuer, qui eius rebum sonet id. In persius voluptatum quaerendum vel, sea quas prima at. Veri facilis adipisci ius ad, choro iracundia instructior at sed. Placerat sententiae per id. Sed voluptua vituperatoribus an, at his nisl viris, eros dictas eleifend ad per! Mazim graeco deterruisset cum id, purto iudico iudicabit ad eum.','a','',0),(14,' Ad justo appetere mei, congue prodesset usu id. Sit omnes iriure gubergren id, aeque tation legendos mel ut, cu legimus convenire philosophia qui? Est cu gubergren rationibus. Quo laudem altera corpora ad, dolores ullamcorper at cum.','b','',0),(15,' Cu mea bonorum volumus, consul voluptatum ex mei! Vero natum menandri per in! Mei omnium iudicabit adversarium cu. Suscipiantur intellegebat eos ex, ei sea partem putent everti.','c','',0),(16,' Sea ex nihil necessitatibus, eu qui latine quaerendum, ei zril officiis tractatos est! Sit eu inani salutandi urbanitas, vis ne officiis volutpat! Illum option ullamcorper ne his, eu verear quaerendum vim. Adhuc noluisse nam eu, ne posse timeam eum! Wisi quaestio eu quo, eos lucilius signiferumque cu.','d','',0),(17,' Vero platonem persequeris te eum, utamur hendrerit vis cu? Eam atqui graecis ceteros cu, et sea scripta oblique. Oratio recteque nec at, nam lorem rationibus ea. Ne tota blandit quo, harum dicunt cotidieque ex mea, usu ad atqui movet senserit. Eu mel alii signiferumque, duo dico laboramus voluptaria in, ne numquam equidem appetere vim! Eam no quas aliquip docendi.','a','',0),(18,' Agam dolorem placerat ex eum, habeo definitiones vim ex, usu ignota adolescens et! Est ad utinam ornatus noluisse! Et vis eius dicant tantas, ea sea ferri ponderum complectitur. Nam minim patrioque at, has solet patrioque ne.','b','',0),(19,' Ne cum autem minim volumus? Novum consulatu signiferumque per ea, vis percipitur conclusionemque ne! Ei posse lorem nam. Nec dicam omnium assentior at, ius ei mazim audiam gubergren.','c','',0),(20,' Epicurei adolescens argumentum his at. Sint nullam dolorem usu te. Te eum duis noluisse recusabo, facer putent aliquip vis ne. Quo regione impedit fastidii id. In has quot unum deserunt, nullam graeco dolorem cu pri.','d','',0),(21,' Ne vel cibo laudem, homero indoctum eam eu. His consul oporteat liberavisse eu! Nec meliore nostrum in. Malis tempor fierent vim in, sed ut choro minimum? Cu eam animal vidisse saperet, vis dico tractatos conclusionemque at, an liber pericula eos.','a','',0),(22,' Amet prima principes ius te, at vivendum efficiantur necessitatibus quo. Ut nec nemore volutpat, et dico oratio munere his? Nemore vocibus pericula no eam, eu vix iudico omnesque. Salutatus posidonium sadipscing usu at.','b','',0),(23,' Eripuit principes quo eu, omnis scaevola sed cu. Falli diceret cum ei, vidit sanctus facilisi vis ei. Vitae expetendis te eum, harum animal ut sit. Malis impedit ex vis, vim invenire argumentum inciderint ei, ne nec idque eripuit?','c','',0),(24,' Per paulo laoreet facilis no, te modus essent nam? Dolor cetero integre quo eu! Vix viris detracto antiopam et. In choro vitae sea. Cu legendos intellegebat sea, ei usu facilisis adolescens. Quod sanctus sensibus mei ex, eos an fugit iisque euripidis, in maiorum interesset disputando sea.','d','',0),(25,' Vidisse vulputate ei eum. Cum nulla accusamus ne, ubique propriae id qui, no mel quem accusamus. Cum ne animal intellegat, pro posse consectetuer no. In eirmod aperiam delicata vel! Dicta albucius et nam, in vero sanctus nominati his.','a','',0),(26,' Augue singulis reprimique no vel, duo at ferri omnesque, est no ubique eirmod? Ea pri eros simul nusquam! Mea id sumo delenit impedit. Nonumy ridens nam ad, at malorum mnesarchum nec! Ne illum equidem nonumes sea, cum te delectus verterem, his fugit fierent an! Praesent tractatos vituperatoribus ut sea.','b','',0),(27,' Ut his saperet disputationi, pri in simul omnes, sumo invenire temporibus has ne. Ea novum ludus scriptorem ius, porro eruditi usu ex? Prima deserunt vim at? Vel ad semper moderatius. Persequeris delicatissimi sed id, eros facilisi at eum.','c','',0),(28,' Ex mel saperet vulputate, ei posse putent concludaturque vix. Has virtute legendos facilisis te. Has eu bonorum habemus, appetere consequuntur his ex, virtute apeirian nec an? No quis sale mediocrem vim. Tota inani ea mei, ei mei sapientem torquatos complectitur? In est tation eripuit nostrum, eu tation numquam intellegam his, usu facete offendit efficiantur ne. Mei an modus summo delectus, appetere pericula ne duo.','d','',0),(29,' Natum mucius placerat mea at. In nec movet exerci erroribus, mea an fabellas contentiones voluptatibus? Per at euismod laboramus, cu fierent incorrupte vix. Vix ei expetendis moderatius, nam an facete voluptatibus?','a','',0),(30,' At eos utroque euripidis? Et duo graece iuvaret splendide, ea perfecto nominati vim? Postea latine theophrastus ea sed. Nec aliquid legendos id, duo dico melius nostrum id! Semper sadipscing et vel.','b','',0),(31,' Complectitur vituperatoribus no vis, esse utamur copiosae cum eu. Te sea tincidunt adversarium, eu nominavi pertinax vim, cu sed vidit assum docendi. Modo aliquid vivendum est ut! At mei wisi choro ceteros, te eam nulla alterum democritum? Graeci sanctus cu pri, pro sumo noluisse at.','c','',0),(32,' Mediocrem vulputate tincidunt in vix, ne sanctus abhorreant repudiandae vim? No sed dico utinam prompta. Mel at vitae democritum, an mel augue patrioque mediocritatem! Placerat convenire cu sed, ignota omittam delicatissimi vim te! Pertinacia instructior ea vel, summo timeam id vis. Ne oblique dolores cum, iracundia assueverit qui cu!','d','',0),(33,' An dolorum albucius sadipscing mea? Et libris patrioque intellegam eam, ei unum reprehendunt conclusionemque eam? Nobis adipisci consequat mea at. Idque propriae assueverit mel et, ex usu agam veri commodo. Odio assum id sed!','a','',0),(34,' Ea eum fugit atqui concludaturque! Diam utinam putent no eos. Illum aliquid ius ea. Eam id case dicant, verear scriptorem duo at, at debet commodo cum.','b','',0),(35,' Quo ne nominati delicatissimi. Et iisque ullamcorper vis, id duo numquam quaestio! Putent democritum sit ut. Id mei animal assentior, est soluta adolescens at.','c','',0),(36,' Ea dicant facete atomorum sit, sed essent iisque dissentiunt ne, possim inimicus disputando id per. An duo dolore indoctum instructior, mea an graece tibique argumentum. Habemus apeirian his cu, ei quod malorum aliquam sed, partiendo posidonium duo et. An modo corpora volutpat qui, vel apeirian forensibus ei!','d','',0),(37,' Vidit oblique blandit quo ne? Paulo constituto te vix, te invidunt temporibus est, mel eu saepe salutandi qualisque? Agam assum simul te mei. Eruditi platonem pericula ei mel, habeo principes eos no! Tantas possim torquatos eu vel. Cu usu ceteros maiestatis constituam, no sed cibo nonumes!','a','',0),(38,' At purto inimicus vulputate vix, elitr gloriatur ei mei, vide accusamus splendide quo in. Numquam docendi ne nam, ut vim meis verear, pericula definitiones ne nec? Omnis aperiam eruditi ad ius. Hinc possit id his. Sensibus definitionem at eum, at mea prima convenire.','b','',0),(39,' An nam dicant partiendo. Pri ignota minimum luptatum ea, cu iisque labitur facilisis eum. Veniam splendide sit et, sanctus convenire reprehendunt ad pri? Sea id mandamus dignissim philosophia, per no natum vituperata, has id eius vidisse. Mutat zril vel et. Ridens possit mediocritatem nec an.','c','',0),(40,' Ut torquatos consectetuer mei, has te dolore aperiri eripuit, vis eros consectetuer ea. Postea vocibus percipitur cum ei? Quo ei esse harum aperiam, legere invidunt ad quo. Vel tota maluisset urbanitas ne, qui an tollit legimus molestie, pro facete equidem in! Liber dicit in sea? In eum nulla elitr?','d','',0),(41,' An mei fuisset concludaturque? Ex legendos scripserit sit, stet iudico ignota eos eu. Tation vocibus his cu, his at aeterno petentium. Nonumy theophrastus ad qui, per no fugit zril omnesque. Vel everti equidem mentitum id, ad enim adversarium his?','a','',0),(42,' Sea harum paulo id, quo ad constituto theophrastus, forensibus elaboraret pri ex. Apeirian convenire quaerendum ius ex, ius amet semper an, nec cu nibh mazim concludaturque. Ne per quas adhuc, at summo definitiones vix? In duo sonet reformidans, ex mei essent eirmod!','b','',0),(43,' In qui oblique numquam adversarium. Causae minimum deseruisse te cum, vis cu elitr denique, cu ius admodum partiendo! Augue vituperata definitiones cum ei, eos lorem atqui ut! Ex vel viris utamur tacimates.','c','',0),(44,' Simul munere graece vis ei? Vim ne habeo primis efficiendi! Ad cibo omittantur mea. Pri amet simul indoctum at, consul option nominati sit ex.','d','',0),(45,' Agam malis tritani nam id, amet inani oblique quo id, quo clita equidem at. At minimum adolescens vix? Eu mutat molestie democritum duo, qui ne vidisse prompta repudiandae, duo affert numquam forensibus id. Mei ea porro prodesset inciderint, has te odio harum. Ius erant elaboraret percipitur at, mei civibus inimicus cu!','a','',0),(46,' Usu id liber intellegat inciderint. Nam justo invenire assentior eu. Eirmod integre eum id, utinam fabellas consequuntur eam ut. Wisi brute senserit his cu.','b','',0),(47,' Mel facer salutatus splendide an! No sea ridens nostrud gubergren? Te eam quis posse, purto fierent mea an, vim ne case autem paulo? Cu debet virtute deserunt est. In iudicabit disputationi vim, luptatum nominati consequuntur vim ei, pri eros nominati aliquando an?','c','',0),(48,' Quas ornatus scaevola eam cu, ex solum modus eos. Pri nominavi verterem at, eam an veri copiosae democritum, doming oportere comprehensam cu mei? Nec ut semper latine dolorem, cu alii illum neglegentur his, an nam utroque moderatius! Ea oblique nostrud est, vivendum maiestatis scribentur sed et, enim dissentiunt cum ei! Sed ex labores inermis nominavi, cu his admodum deserunt ullamcorper? Per errem choro scripta cu.','d','',0),(49,' Id pro dico prima epicuri, pri dolore nostrud patrioque ad, an duo iriure utamur. Te periculis eloquentiam quo, ad his mutat laudem, ex mel affert verear. Congue accusamus et vel, praesent dissentias ex mea, ea mei iudico graece sententiae. Malorum nostrud honestatis et est, libris molestiae scribentur vis id! Fuisset antiopam dissentiunt vim at, ut vim sumo tempor vivendum, iuvaret partiendo no his? Diam dicant recusabo sea id, prima epicuri usu ne? Nam aperiam consetetur scripserit ea.','a','',0),(50,' Est ad verear pertinacia, eos cetero diceret abhorreant no. Vide possim vel ut, nullam deleniti vix ex? Te mazim dictas eos, at viris periculis mei, omnis harum te eum. Ius ut tantas aliquando reprimique, no vis munere verear animal, quas nostrum ad pro? Est no audire maluisset, ad pri veri vidit periculis.','b','',0),(51,' In tantas soleat intellegat quo, omittam consequuntur est ea, ius id affert exerci labitur. At sed habeo ocurreret consectetuer, animal commune eu vel, modus denique eam et! Per choro impetus voluptaria an, eu pro corrumpit appellantur? Cu veri explicari duo, inani commune his ei. Habeo iusto fabulas mei cu, mel oporteat scripserit an! Est in harum fuisset, mei ad intellegam interesset theophrastus.','c','',0),(52,' Nonumes percipitur an pri. Mea ne modo aeterno suavitate? Nam singulis tacimates et. Eos et iusto dicant, adhuc electram maiestatis est ut, sit alterum volumus cu? Est cu nihil putent, nec assum consequat aliquando eu, vis ubique labitur epicurei ea?','d','',0),(53,' Pro purto lucilius necessitatibus eu, nec no assum meliore. Tamquam eleifend sententiae vis ad? Ad verear omittantur suscipiantur sed, elit molestie eu vim, veniam neglegentur intellegebat eos eu. Mei id adhuc tempor reprehendunt.','a','',0),(54,' Per ut doctus ornatus interesset, eos graeco dolores scaevola et, quot vitae reprimique sed te! Tempor animal sit at! Mel ut regione mentitum definiebas? Ad vim agam omnium. Paulo ocurreret mel at. Ferri nostro corrumpit ne vis, eius homero omnesque nec at, ea tollit officiis est.','b','',0),(55,' Id utamur vivendo qui, inermis molestiae omittantur an duo? At case possit commune mel. Case graecis iudicabit est no. At dolores neglegentur mei.','c','',0),(56,' Pri eruditi appareat convenire ut. Harum accumsan ei vis? An mea copiosae adolescens philosophia. Postea erroribus nam cu, est hinc postea fabulas et, vix ea nemore virtute fabulas. Purto ornatus legimus ius cu, at sea probo dolor accumsan.','d','',0),(57,' Eam suscipit rationibus an. Id vel appetere persecuti intellegam, mel te tota quando, cum ea augue reprehendunt! Has in cetero dissentias repudiandae, per percipit pericula gloriatur in, eum unum prodesset cu. Assum debet dolorem eu his, pro error mediocrem no! Ei apeirian contentiones mel, cu dicit prompta sit.','a','',0),(58,' Quo te graeci mediocrem. Nec ut illum assum lobortis, in vix imperdiet voluptatum, tale vivendum et sea. His quot adipiscing ea, viris appareat theophrastus duo ea, at sea affert voluptatum! Odio officiis vituperata vis an. Sea errem aeterno in, aliquam dissentiunt te his. Alienum rationibus an ius, pri quando sapientem euripidis in.','b','',0),(59,' Eu qui eripuit pertinacia consetetur, nam ignota equidem volutpat eu, has sapientem democritum no. Autem ceteros efficiantur ei pri, te odio noluisse phaedrum vel. Te vel unum sonet elitr. Illud pericula neglegentur ius eu, no vix tacimates referrentur, ex libris semper mel? Dicta zril per id, audiam debitis accommodare et est?','c','',0),(60,' Sale oblique disputando no ius. Cum habeo numquam eu, error tibique conceptam per ex. Ludus aeterno menandri ei cum, his euismod vivendum platonem cu! Malis putant singulis cu sit, nam ea diam adversarium.','d','',0),(61,' Id nobis dictas recusabo ius, has graece gubergren at. Mei debet oblique te! Ne verear admodum vulputate mei. Te vim dolor scribentur, duo rebum graecis expetendis eu? Ex wisi modus mei, ius an novum regione signiferumque. Vim diam quaestio platonem an. In nulla simul signiferumque sea, et mutat scaevola nam?','a','',0),(62,' Case consul an usu, no ocurreret laboramus dissentiet has! Suas sint munere ut eam. Id reque justo maluisset quo, cum tation munere at? Ex oblique percipit mel, te sed alii reque efficiantur, liber abhorreant an mei. Mei integre dolorem ei! His at alii senserit, ne vel ferri nulla reformidans?','b','',0),(63,' Quot copiosae senserit ad sit, illum impedit ad eam? Rebum liber recusabo te eum. An mentitum evertitur pertinacia eum, mei probo denique invenire in, mel te prima maluisset consectetuer! Quem legendos ex est, in habemus efficiendi sit, at quo idque antiopam?','c','',0),(64,' Errem accusam consequuntur an sit, reprimique liberavisse his no, modo enim tacimates mea ei? Quis fabellas an pro, possit delenit inciderint ne eam. Et etiam recteque convenire vis, eam viris altera scribentur te, ad his error oportere molestiae. Eu sententiae concludaturque duo, agam omnesque dissentiunt cu nam. Alienum adipisci referrentur nam et. Id est etiam viris platonem, eum ut mundi labore.','d','',0),(65,' Solum ponderum oportere cum ei. Ut probo qualisque nam? An has harum civibus mnesarchum, illum accusata voluptaria cu vis! Affert similique sit no, eos at homero albucius voluptaria, et cibo explicari eam. Sumo maiestatis appellantur no pro, ius ut legere corrumpit pertinacia. Modus perfecto reprehendunt ex qui.','a','',0),(66,' Ex sit sumo tincidunt, veri etiam option id sed. Eam tale scaevola eu, quo cu alienum euripidis accommodare, sed in suas paulo adipiscing. Aliquip aliquando omittantur ex eum, volumus laboramus eum an. Debet lucilius sea ne, mei eu eros semper, eos ea suas maiorum? His et quod causae utamur.','b','',0),(67,' Eum ne possim mnesarchum? In nominati philosophia eum, sea eligendi similique sententiae ne? An quo magna tamquam tacimates, ius amet facilisis at? Et eam vituperata sadipscing, sea ne probatus abhorreant, vero erat dicat ne has!','c','',0),(68,' Vix nibh accusata quaestio eu? Sit id meis ludus nostrud. At agam denique nec! Usu fastidii gloriatur sadipscing at? Et dicit deleniti duo. Ius mandamus voluptatum no, qui ne illum volutpat repudiandae, est ne aliquando definiebas.','d','',0),(69,' Per recteque voluptatum liberavisse et, id vel liber simul graecis? Ei corpora constituam qui, ex efficiantur intellegebat ius. Te rebum laboramus ius, ei cum magna albucius signiferumque. Mei id nostrud abhorreant, reprimique suscipiantur vis ea! Ius putant deterruisset no, eu mea alia velit.','a','',0),(70,' Sea recteque appellantur no. No habemus accumsan conceptam eam, viris meliore per te? Suas diceret expetendis ad pri! Ei ubique malorum eos. Et lobortis reprimique duo, nobis facete corpora ne sea, sea te zril perfecto! Te nam nisl erat suscipiantur, sed aperiam blandit deserunt te, ex etiam veritus his.','b','',0),(71,' Eam verterem detraxit euripidis ex, sit congue mucius menandri ad. Et commodo aeterno nec. Pri ad suas dicant mentitum. Impetus fastidii nec te.','c','',0),(72,' Sea et prompta omnesque periculis, modo doctus est in, dicta populo conclusionemque nam et. No pro audiam accusata. In autem dicta virtute usu, hendrerit consequat vel ei. Vide nonumes inciderint ea nec, no invidunt detraxit splendide est. Nisl dicat aperiri ex pri!','d','',0),(73,' Perpetua scripserit suscipiantur his ad. Partem diceret torquatos eum et, no vim rebum fabulas delicata! In per porro liber assentior. Ea semper veritus dissentias duo, te pri commodo iudicabit.','a','',0),(74,' Pri ad omittam omnesque appellantur? Habemus electram te has! Cu pro wisi fabellas? Ne animal expetenda torquatos mei, movet referrentur an has, te nam dicunt iuvaret.','b','',0),(75,' Paulo noster accusata ex duo. Nisl apeirian necessitatibus qui ea. Splendide omittantur definitiones sea ut, voluptua oporteat adversarium eu vis. Iudico menandri ex sed, sed rebum salutandi cu. Adhuc everti an vel, sit no malis tation fabellas, mel at eros error deterruisset?','c','',0),(76,' Ne vel prompta alienum, eu quando percipitur efficiantur qui! Vis fugit complectitur eu, sea omittam mnesarchum cu, an salutatus voluptatum usu. Ex sed audire eruditi torquatos, no mea harum bonorum? Qui quod probo everti ex, mutat integre voluptatibus mel te.','d','',0),(77,' Erant ridens apeirian mei ei. Id vocent deseruisse intellegam est! Verterem persecuti sadipscing ei eum, sonet nihil tractatos eam id, cu augue decore pri. Vix ex aliquip legendos accommodare. An vide copiosae accusata has, odio eripuit in pri, ne mei quando commune honestatis? Te enim impedit oporteat mei!','a','',0),(78,' Pri ne esse dolorum interpretaris? Cum fabellas delicata sapientem eu, mea te molestie phaedrum! Sea te erant reprimique! Ne vel iuvaret noluisse fabellas, suas utroque eum eu. Eu populo ponderum eum, iisque urbanitas intellegat an nec.','b','',0),(79,' Mel at mundi consequat honestatis, est sonet prodesset deseruisse ne. Tibique placerat torquatos duo cu, cu has eligendi mnesarchum, ubique scriptorem id nec. Est nulla expetendis ei? Possim fabellas usu ex.','c','',0),(80,' No prima utinam vis! Ex vim nulla malorum vivendo, mei purto omnes corrumpit ut, minim dictas accommodare ne vix. Eam eu viris tantas senserit, veri assum ne vel, ea pro elit interpretaris? Usu virtute lobortis at, at est agam prompta, alia vidit vim id. Tollit urbanitas ex eum, ex has sonet dolore nostro. Usu te conceptam interesset eloquentiam!','d','',0),(81,' Nobis maiestatis vis ut. Cu putent suscipiantur comprehensam his, erat consequuntur at mei, ea delectus definitiones mel. Te ius aperiam nusquam consectetuer! Cu explicari intellegat mel, eum ei dolore facilis posidonium.','a','',0),(82,' Eam id natum omnesque moderatius, ex solet aperiam efficiendi vim. Audire apeirian instructior est ea, ius meliore ceteros cu. Ex mea possit oportere splendide, eu molestie praesent nec? Ex nibh animal cum, luptatum evertitur ea nam? Ei vivendum intellegam sea, mei tota oblique ut. Elitr scaevola percipit at usu.','b','',0),(83,' Ut mollis laoreet sea, ius cu quem salutatus. Dicta invidunt menandri cum an, eam te agam atomorum. Eum omnis convenire patrioque eu, ut utroque offendit eloquentiam sea, recusabo ullamcorper eos eu. Duis consul vix ei, illum utamur debitis per te. Vim dicat alienum tacimates ut. Commodo nostrum te pri, semper iisque explicari eu pri?','c','',0),(84,' An atqui nonumy ancillae mea! Ei placerat nominati incorrupte sed, cu eam dicant convenire? Cu nec modus sanctus definitiones, no altera platonem iudicabit vim. An pro discere postulant. Tollit civibus eum ad.','d','',0),(85,' Porro timeam adipiscing cu eam? Eum tempor adolescens id, everti omnesque scriptorem cum in. Ea eum ignota scaevola suscipiantur, te ipsum elaboraret sea, ne pri aperiam inermis. Ne facer aeterno assentior ius, vix dolores adipiscing te, admodum consectetuer ei mei. Vis id odio natum abhorreant, vim id lorem malis saepe.','a','',0),(86,' Sea tibique indoctum percipitur ne. Mei eros melius ea, error referrentur et sea, no duo soleat tibique. In interesset comprehensam eam, ius an error oporteat probatus. Ne vis habeo mandamus, an mei volumus delectus, est ea postea perfecto.','b','',0),(87,' Vim molestie disputationi reprehendunt ne, eam graecis menandri torquatos ex. Vis ea soluta ornatus intellegam. Sed no oblique rationibus? Essent consulatu mea ne.','c','',0),(88,' Mel ei vidit eripuit scaevola, eu malorum delectus suscipiantur quo! Nam invenire salutatus conceptam ut, sonet accumsan dissentiunt ei mel. At duo nobis dicant, in sea phaedrum tractatos? An mei iusto dolore omittam, te harum hendrerit vim.','d','',0),(89,' Alia probo aperiam vim et, te nam movet disputando. Per ei doctus disputationi interpretaris, rationibus incorrupte ut per, eum ad putant quaeque appellantur. No est ubique aliquid dolorum, mei at homero essent contentiones! Natum veniam vel ut.','a','',0),(90,' Augue ludus pericula eu vix, debet luptatum qui cu. Quando pertinacia intellegebat nam ex, ne sea nonumy altera apeirian. Id nam tale suas tation, tale populo qui ea? Eu qui mundi erroribus percipitur, pro no audire tibique accommodare, id ius impedit lucilius. Ex sadipscing philosophia vim. Ut qui appetere iracundia evertitur.','b','',0),(91,' Ex pri illud erant partiendo! Modo inermis his ne, mea cu tale solum nusquam. Has id delectus repudiare, ea audiam denique vim, ut sea duis rationibus! Nec in animal commodo similique.','c','',0),(92,' Nam vero singulis te, ei sit prima iisque constituam. Sed ex repudiare instructior, ius ne audire impetus, te natum dicant nonumy pro? Usu ne reque democritum delicatissimi, impetus admodum pertinax pri ei, per esse altera intellegat id. His sumo eligendi periculis eu. Movet scripta mandamus cum ad, cu mel vidit animal nusquam, has sumo lorem pertinax no!','d','',0),(93,' Graecis accusam dolores vix id, vocent vituperatoribus ei ius, pro saperet inermis volumus no. Ignota utamur gloriatur an pri! Mea id molestie appetere! Ea nonumy luptatum dissentiunt mea? Quis intellegam ad mea.','a','',0),(94,' Ei nec eirmod erroribus necessitatibus. Eu his habeo tamquam? Eum tota primis legendos et, id est agam purto ipsum? Cum no quaeque reformidans, ea scripta prompta pro, illum eripuit an ius. Eripuit feugait cu has, cum cu libris gubergren consectetuer. Fugit dicta ornatus cum ad, nostrum disputando ut ius, populo omnesque vix ne.','b','',0),(95,' Saperet consequuntur quo et, cu nisl modo modus nec! Duo no vocent aliquip scribentur, ius solet perfecto repudiare ei, audiam legendos nec ne! Ei ullum aperiam has, putent vivendum mediocritatem ex ius. Lobortis convenire vulputate duo ne, melius legendos nec an. Numquam quaestio et has, iusto albucius menandri ea vix? Saperet pertinax mei ne. Te quas menandri nam?','c','',0),(96,' Brute autem no sea. Mei ei impetus delicata scriptorem, prompta sensibus ea has. Ea sit lucilius atomorum, latine mentitum salutandi eum cu! Ius nominati suscipiantur in. Ut vim alterum dolores mediocrem, sit at rebum constituam sadipscing!','d','',0),(97,' Mei hinc aeterno at, accumsan assentior mediocritatem has ut. In vim diam dissentias! Ius dicant nostro minimum in, soleat posidonium an vel, eos ad habemus lucilius convenire! Prima alienum sea ei, iracundia adversarium an his, et causae appareat mei. Tempor reformidans mel eu, id dolores delicatissimi nec, et urbanitas similique repudiandae eam. Ius unum erat error in, dicant maiorum vis eu.','a','',0),(98,' Similique adversarium vis id, ne duo eirmod epicuri maluisset, cum ut modus partem ocurreret. Amet sadipscing in sit. Pri no alii dolorem consequat, sea ne tation eripuit accusamus, at idque exerci perpetua sea. Mea eu debitis epicurei? Et mel vivendum tacimates? Probo inani insolens vix ex, eu adipisci complectitur comprehensam vis, dico brute viris eu duo.','b','',0),(99,' His utamur equidem ea! Ceteros commune repudiandae eu eam, cum latine reprimique cu. Porro labore definiebas et nam, his at brute dicat! Ne per dico quaeque, cu quidam inimicus sapientem eum, ex est veritus vituperatoribus.','c','',0),(100,' Eu eam liber bonorum, eum nominavi definitiones an, cum ea falli fabulas. Appareat constituto ne est, per solet dissentias et. Eum prompta qualisque ex, nam eu dicat invidunt rationibus. His causae platonem perpetua no, ut impetus noluisse sea.','d','',0),(101,' Te quo hinc insolens eloquentiam, ut vix nostro verterem, ut has erat offendit menandri. Cum eu reque dictas! Te ius legere fuisset aliquando, ut mel evertitur incorrupte. Eirmod virtute adversarium pro et, singulis ocurreret nam id. Omnis fabellas ex quo, est ex alii aliquip? Facilis albucius perpetua per te, est et ubique accusam!','a','',0),(102,' Sit ex malis dicta electram, eius virtute ex cum, eum dicat accusamus sadipscing ut. Mea vidit minimum mediocritatem ad, ludus volutpat mnesarchum ea vim. Atqui assentior eum id. Eu his esse impedit vocibus.','b','',0),(103,' Pri at tollit insolens concludaturque, ex utinam omittam vis. Id persius equidem theophrastus vix, assum fastidii euripidis nam an, quo porro incorrupte cu. Id has melius tritani conceptam, delicata repudiare reformidans id mel! Usu at lobortis ocurreret? No vix option iuvaret. Eam cu hendrerit scripserit, eu quaestio consectetuer qui, ad mei suas choro apeirian?','c','',0),(104,' Ei vidisse deserunt perpetua mea, te mel omnes soleat conclusionemque, ludus menandri eum ut. No idque ancillae sed, eleifend pericula qui no. Ius eu dicit doming definiebas, eum percipitur signiferumque in? Eu nec inciderint sadipscing, novum munere cu eos?','d','',0),(105,' Soleat vituperata usu ea, quo an odio dicam deseruisse. Odio fuisset ea est, latine vocibus intellegebat in est! Quaeque phaedrum necessitatibus id cum. Nec augue dolores persequeris ne.','a','',0),(106,' His iriure maluisset voluptatum id, vim ei habeo audire. His an vocent offendit, ea mei docendi concludaturque? Labore scribentur vis ut, cu vis quod disputando? In solum iusto pertinacia per? Dolore laoreet ei ius?','b','',0);
/*!40000 ALTER TABLE `testquestions_ip_1` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-09-20  9:01:28
