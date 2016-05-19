//========== populate events ==========/
events.add(
		new Event(
				new Time(0, 0, 0), 
				incidents.get(187), 
				new Properties(
						new Array(
								new Property("Telephone Conversation:", new Array("Student Action:", "Places a call to CHP Dispatch to notify of stalled DOT vehicle.")),
								new Property("Maintenance Radio:", new Array("Dispatch, this is xxx. I'm on south 55 at the 405. My truck is out of service here, stuck in the lane. I need CHP assistance ASAP!"))
						)
				),
				new Evaluations(new Array())
		)
);
events.add(
		new Event(
				new Time(0, 1, 0), 
				incidents.get(187), 
				new Properties(
						new Array(
								new Property("CHP CAD:", new Array("Detail:", "STALLED DOT SB SR-55 AT I-405"))
				
						)
				),
				new Evaluations(new Array())
		)
);
events.add(
		new Event(
				new Time(0, 2, 0), 
				incidents.get(187), 
				new Properties(
						new Array(
								new Property("CHP Radio:", new Array("Dispatch:", "14-14 Santa Lucia", "Field:", "Santa Lucia 14-14 go ahead", "Dispatch:", "14-14 Santa Lucia 11-25 stalled DOT truck southbound 55 overpass at 405.", "Field:", "Santa Lucia 14-14 10-4. Enroute from 55 at Dyer Rd.", "Dispatch:", "14-14 Santa Lucia copies enroute from 55 at Dyer Rd."))
						)
				),
				new Evaluations(
						new Array(
								new Evaluation("ATMS", new Array("Expected Action", "Operator viewing cameras in incident area.")),
								new Evaluation("CAD", new Array("Expected Action", "Operator should be verifying incident 187 on CAD"))
						)
				)
		)
);
events.add(
		new Event(
				new Time(0, 3, 0),
				incidents.get(187),
				new Properties(
						new Array(
								new Property("CHP Radio:", new Array("Dispatch:", "14-14 Santa Lucia", "Field:", "Santa Lucia 14-14 go ahead", "Dispatch:", "14-14 Santa Lucia. Cellular 911 caller reports stalled vehicle on the 55/405 overpass was hit and a vehicle was pushed over the side. Can you verify?", "Field:", "Santa Lucia, negative, I'm one minute away.", "Dispatch:", "14-14 Santa Lucia 10-4. One minute away.")),
								new Property("CHP CAD", new Array("Detail:", "CELLULAR 911 RPT DOT HT, 2ND VEH OVER THE SIDE, UNVERIFIED"))
						)
				),
				new Evaluations(new Array())
		)
);
events.add(
		new Event(
				new Time(0, 3, 30),
				incidents.get(187),
				new Properties(
						new Array(
								new Property("CHP CAD", new Array("Detail:", "DUPLICATE CALL"))
						)
				),
				new Evaluations(new Array())
		)
);
events.add(
		new Event(
				new Time(0, 4, 30),
				incidents.get(187),
				new Properties(
						new Array(
								new Property("CHP CAD", new Array("Detail:", "DUPLICATE CALL"))
						)
				),
				new Evaluations(new Array())
		)
);
events.add(
		new Event(
				new Time(0, 5, 0), 
				incidents.get(187), 
				new Properties(
						new Array(
								new Property("CHP Radio:", new Array("Field:", "Santa Lucia 14-14", "Dispatch:", "14-14 Santa Lucia go ahead.", "Field:", "Santa Lucia 14-14 10-97 11-83 11-25. It appears the DOT truck was hit by a vehicle and over the side landed in the northbound 405 lanes below. A major collision occurred below. Request 11-41 Code 2 and a couple units to assist.", "Dispatch:", "14-14 Santa Lucia 10-4 10-97 11-83 11-25. DOT truck was hit by vehicle and the vehicle over the side landed in the northbound 405 lanes below. A large collision occurred below. Request 11-41 Code 2 and a couple units to assist.")),
								new Property("CHP CAD:", new Array("Detail:", "DOT HIT BY VEH, VEH OVER SIDE.", "Detail:", "2ND VEH LANDED NB 405 BELOW, LG COLLISION OCCUR. REQ 2 1141 CODE 2 UNITS ASSIST"))
						)
				),
				new Evaluations(
						new Array(
								new Evaluation("CMS", new Array("Target CMS:", "72 - SB 55 @ WARNER AVE", "Sample Message:", "ACCIDENT AHEAD / AT 405 OVERPASS")),
								new Evaluation("Facilitator", new Array("Expected Action:", "Should be responding to incident 187", "Expected Action", "Should be monitoring ATMS operator"))
						)
				)
		)
);
events.add(
		new Event(
				new Time(0, 5, 30),
				incidents.get(187),
				new Properties(
						new Array(
								new Property("CHP CAD", new Array("Detail:", "DUPLICATE CALL"))
						)
				),
				new Evaluations(new Array())
		)
);
events.add(
		new Event(
				new Time(0, 6, 0), 
				incidents.get(187), 
				new Properties(
						new Array(
								new Property("CHP Radio:", new Array("Dispatch:", "14-17, 14-9 Santa Lucia", "Field_1", "Santa Lucia 14-17 go ahead.", "Field_2", "Santa Lucia 14-9 go ahead.", "Dispatch:", "14-17, 14-9 assist 14-14 11-83 11-25 northbound 405 at the 55 interchange.", "Field_1", "Santa Lucia 14-17 copies. Enroute from 405 just south of Macarthur Boulevard.", "Field_2", "14-9 10-4, enroute from 55 at Edinger Avenue.", "Dispatch:", "Santa Lucia copies 14-17 enroute from 405 just south of Macarthur and 14-9 enroute from 55 at Edinger.")),
								new Property("CHP CAD:", new Array("Detail:", "14-17 14-9 ENRT ASSIST 1183 1125"))
						)
				),
				new Evaluations(new Array())
		)
);
events.add(
		new Event(
				new Time(0, 6, 30),
				incidents.get(187),
				new Properties(
						new Array(
								new Property("CHP CAD", new Array("Detail:", "DUPLICATE CALL"))
						)
				),
				new Evaluations(new Array())
		)
);
events.add(
		new Event(
				new Time(0, 7, 0), 
				incidents.get(187), 
				new Properties(
						new Array(
								new Property("CHP Radio:", new Array("Field:", "Santa Lucia 14-14", "Dispatch:", "14-14 Santa Lucia go ahead.", "Field:", "Santa Lucia, the driver of the DOT truck is uninjured but the #2 and #3 lanes are now blocked on southbound 55. I'm moving down to the 405 lanes to assess the damage.", "Dispatch:", "14-14 Santa-Lucia 10-4. Driver of the DOT truck is uninjured. The #2 and #3 lanes are now blocked on southbound 55. Moving down to the 405 lanes to assess the damage.")),
								new Property("CHP CAD:", new Array("Detail:", "DOT DRIVER UNINJURED, #2,3 LNS BLCKD SB 55.", "Detail:", "14-14 MVNG TO 405 ASSESS DMG"))
						)
				),
				new Evaluations(
						new Array(
								new Evaluation("CMS", new Array("Target CMS:", "72 - SB 55 @ WARNER AVE", "Sample Message:", "ACCIDENT AHEAD / AT 405 OVERPASS / 2 RT LANES BLKD")),
								new Evaluation("Activity Log", new Array("Expected Action:", "Should be updating the incident board."))
						)
				)
		)
);
events.add(
		new Event(
				new Time(0, 8, 0),
				incidents.get(187),
				new Properties(new Array()),
				new Evaluations(
						new Array(
								new Evaluation("Telephone Conversation", new Array("TV Reporter #1:", "Hello, this is Cameron Stevens at KCLY TV channel 2.", "TV Reporter #1:", "I was told that a car flew off an overpass causing a large pile-up on the freeway below.", "Student Action:", "Incident should be confirmed", "TV Reporter #1:", "I want to confirm the location so we can send a helicopter to check it out.", "Student Action:", "55/405 interchange"))
						)
						
				)
				
		)
);
events.add(
		new Event(
				new Time(0, 10, 0), 
				incidents.get(188), 
				new Properties(
						new Array(
								new Property("CHP Radio:", new Array("Dispatch:", "9-9 Santa Lucia", "Field:", "Santa Lucia 9-9 go ahead.", "Dispatch:", "9-9 Santa Lucia 11-79 northbound 5 just north of Lake Forest Drive.", "Field:", "Santa Lucia 9-9 copied enroute from 405 at Irvine Center.", "Dispatch:", "9-9 Santa Lucia 10-4. Enroute from 405 at Irvine Center.")),
								new Property("CHP CAD:", new Array("Detail:", "9-9 ENRT FRM I405 @ IRVINE CTR"))
						)
				),
				new Evaluations(
						new Array(
								new Evaluation("ATMS", new Array("Expected Action:", "Operator should try to confirm the incident with a CCTV camera.")),
								new Evaluation("CAD", new Array("Expected Action:", "Operator should be monitoring the CAD")),
								new Evaluation("Facilitator", new Array("Expected Action:", "Should verify location and respond to incident 188"))
						)
				)
		)
);
events.add(
		new Event(
				new Time(0, 11, 0),
				incidents.get(187),
				new Properties(new Array()),
				new Evaluations(
						new Array(
								new Evaluation("Telephone Conversation", new Array("Radio Reporter #1:", "Hello, this is Dakota Crew at KCOW radio.", "Radio Reporter #1:", "I just heard over the scanner that you have a major accident on I405 at 55.", "Student Action:", "Should confirm incident.", "Radio Reporter #1:", "Can you give me a run down on what happened?", "Student Action:", "A collision occurred on NB 55 at 405. A vehicle went over rail onto NB 405 causing a 6 car collision."))
						)
						
				)
				
		)
);
events.add(
		new Event(
				new Time(0, 12, 0), 
				incidents.get(188), 
				new Properties(
						new Array(
								new Property("CHP Radio:", new Array("Field:", "Santa Lucia 14-14", "Dispatch:", "14-14 Santa Lucia go ahead.", "Field:", "Santa Lucia 14-14 10-97. Multiple 1144 northbound 405 at 55 overpass. Six vehicle collision blocking the three inside lanes. The driver and passenger of the car that went over the railing are both 11-44. I've got two other fatals and multiple injured down here. Request coroner and issue Sig Alert.", "Dispatch:", "14-14 Santa Lucia 10-4 10-97 11-80 11-25 northbound 405 at 55 overpass. Multiple 1144. Six vehicle collision blocking the three inside lanes. The driver and passenger of the car that went over the railing are 11-44, 2 other 11-44. Multiple injuries, request coroner and send Sig Alert.")),
								new Property("CHP CAD:", new Array("Detail:", "14-14 1097, MULTIPLE 1144 NB 405 AT 55 OVERPASS, 6 VEH TC", "Detail:", "BLOCKING 3 INSIDE LNS NB 405. DRVR AND PSSNGR IN VEH 1144, 2 OTHER FATALS, MULT INJR, REQ CRNR AND SIGALERT"))
						)
				),
				new Evaluations(
						new Array(
								new Evaluation("CMS", new Array("Target CMS:", "87 - NB 405 @ HARVARD", "Sample Message:", "ACCIDENT AT HWY-55 / 3 LEFT LNS BLKD")),
								new Evaluation("CMS", new Array("Target CMS:", "88-NB405@ICD", "Sample Message:", "ACCIDENT AT HWY-55 / 3 LEFT LNS BLKD")),
								new Evaluation("Activity", new Array("Expected Action:", "Should fax and page sig alert information"))
						)
				)
		)
);
events.add(
		new Event(
				new Time(0, 12, 30), 
				incidents.get(187), 
				new Properties(
						new Array(
								new Property("CHP CAD:", new Array("Detail:", "1039 CRNR"))
						)
				),
				new Evaluations(
						new Array(
								new Evaluation("Activity Log", new Array("Expected Action:", "Should insert info for enroute coroner"))
						)
				)
		)
);
events.add(
		new Event(
				new Time(0, 13, 0), 
				incidents.get(187), 
				new Properties(
						new Array(
								new Property("CHP Radio:", new Array("Field:", "Santa Lucia 14-17", "Dispatch:", "14-17 Santa Lucia go ahead.", "Field:", "Santa Lucia 14-17 10-97. 1144 northbound 405 at 55. Paramedics are 10-97. We've gotta secure the scene southbound 55 for the DOT hazard. Has Caltrans been notified that their truck has been involved in a collision?", "Dispatch:", "14-17 Santa Lucia 10-4 10-97 11-44 northbound 405 at 55. Paramedics 10-97. Will be doing 11-84 on southbound 55 for DOT collision. Caltrans will be notified.", "Field:", "Santa Lucia 14-17 10-4")),
								new Property("CHP CAD", new Array("Detail:", "14-17 1097, PARAMEDICS 1097. 1184 SB 55 FOR DOT 1125"))
						)
				), 
				new Evaluations(new Array())
		)
);
events.add(
		new Event(
				new Time(0, 14, 0), 
				incidents.get(188), 
				new Properties(
						new Array(
								new Property("CHP Radio:", new Array("Field:", "Santa Lucia 9-9", "Dispatch:", "9-9 go ahead Santa Lucia", "Field:", "Santa Lucia 9-9 10-97 11-79. Fire department and parmedics 10-97. Collision between semi truck carrying tomatoes and vehicle in 2 right lanes. Truck on fire and tomatoes spilled over three right lanes. Request Caltrans to clean up the tomatoes and units for 11-84.", "Dispatch:", "Santa Lucia 10-4, 9-9 10-97 11-79. Fire and paramedics 10-97. Collision involves tomato truck and car in two right lanes. Truck on fire and tomatoes over three right lanes. Request Caltrans.")),
								new Property("CHP CAD:", new Array("Detail:", "9-9, FD, PARAMEDICS 1097. TC SEMI W/TOMATOES AND VEH ON NB-5 #3,4,5 LNS", "Detail:", "TRCK ON FIRE, REQ CT CLNUP, UNITS 1184"))
						)
				),
				new Evaluations(
						new Array(
								new Evaluation("CMS", new Array("Target CMS:", "31 - ??", "Sample Message:", "UR TIRES R RND")),
								new Evaluation("Activity Log", new Array("Expected Action:", "Should fax and page information"))
						)
				)
		)
);
events.add(
		new Event(
				new Time(0, 14, 30), 
				incidents.get(187), 
				new Properties(
						new Array(
								new Property("CHP Radio", new Array("Field:", "Santa Lucia 14-14", "Dispatch:", "14-14 Santa Lucia go ahead.", "Field:", "Santa Lucia, request MAIT, they'll have to investigate this one. Also, request 14-S unit and TMT.", "Dispatch:", "14-14 Santa Lucia copied request MAIT, 14-S unit, and TMT.", "Dispatch:", "14-S3 unit copy.", "Field:", "Santa Lucia 14-S3, 10-4 enroute from 73 at Bear Street.", "Dispatch:", "14-S3 Santa Lucia copies enroute from 73 at Bear Street.")),
								new Property("CHP CAD:", new Array("Detail:", "REQ MAIT, REQ 14-S UNIT, REQ TMT")),
								new Property("TMT Radio:", new Array("TMT leader espond to student action. Dispatch appropriate units???"))
						)
				),
				new Evaluations(
						new Array(
								new Evaluation("Facilitator", new Array("Expected Action:", "Should be alert and advising radio on TMT."))
						)
				)
		)
);
events.add(
		new Event(
				new Time(0, 15, 0), 
				incidents.get(187), 
				new Properties(
						new Array(
								new Property("Telephone Conversation:", new Array("Radio Reporter #2:", "Hello, this is Monica Stevens at KCLY radio.", "Radio Reporter #2:", "I called earlier about the accident on I405. Have any more details come in on that incident?", "Student Action:", "Vehicles blocking 3 NB 405 lanes, 4 dead and 7 injured.", "Radio Reporter #2:", "Do you have an alternate route I can recommend?", "Student Action:", "Yes (TBD)"))
						)
				),
				new Evaluations(new Array())
		)
);
events.add(
		new Event(
				new Time(0, 16, 0), 
				incidents.get(187), 
				new Properties(
						new Array(
								new Property("Maintenance Radio:", new Array("If Maintenance unit was sent out, radio the supervisor with a 10-97. Say that you're going to await MAIT investigation and transport other Maintenance worker."))
						)
				),
				new Evaluations(new Array())
		)
);
events.add(
		new Event(
				new Time(0, 17, 0), 
				incidents.get(187), 
				new Properties(
						new Array(
								new Property("CHP Radio:", new Array("Field:", "Santa Lucia 14-9.", "Dispatch:", "14-9 Santa Lucia go ahead.", "Field:", "Santa Lucia 14-9 10-97. Will assist 14-14 with 11-84", "Dispatch:", "14-9 Santa Lucia 10-4 10-97 northbound 405 at 55. Will assist 14-14 with 11-84.")),
								new Property("CHP CAD:", new Array("Detail:", "14-9 1097, ASSIST 1184"))
						)
				),
				new Evaluations(new Array())
		)
);
events.add(
		new Event(
				new Time(0, 20, 0), // orginally at 00:18:00 
				incidents.get(187), 
				new Properties(
						new Array(
								new Property("CHP Radio:", new Array("Field:", "Santa Lucia 14-17.", "Dispatch:", "14-17 Santa Lucia go ahead.", "Field:", "Santa Lucia 14-17. Traffic is backing up on southbound 55. Request TMT and medium duty 11-85 for the DOT truck.", "Dispatch:", "14-17 Santa Lucia 10-4. Traffic backing up on southbound 55. Requesting TMT and medium duty 11-85.")),
								new Property("CHP CAD:", new Array("Detail:", "TRAFFIC BACKING SB 55 REQ TMT MEDIUM DUTY 1185"))
						)
				),
				new Evaluations(new Array())
		)
);
events.add(
		new Event(
				new Time(0, 22, 0), // orginally at 00:19:00 
				incidents.get(187), 
				new Properties(
						new Array(
								new Property("CHP Radio:", new Array("Field:", "Santa Lucia 14-14", "Dispatch:", "14-14 Santa Lucia go ahead.", "Field:", "Santa Lucia 14-14. 4 11-44's, 2 11-80's, and 4 11-81's.", "Dispatch:", "14-14 Santa Lucia copies 4 11-44's,2 11-80's, and 4 11-81's.")),
								new Property("CHP CAD:", new Array("Detail:", "4 1144'S, 2 1180'S, 4 1181'S")),
								new Property("Telephone Conversation:", new Array("TV Reporter #2:", "Hello, this is Fred Roppel at KNOW TV.", "TV Reporter #2:", "Have any more details come in on the pileup at 55/405?", "Student Action:", "Vehicles blocking 3 NB 405 lanes, 4 dead and 7 injured, ??? mile backup", "TV Reporter #2:", "Do you have an alternate route I can recommend?", "Student Action:", "(pause for answer: Yes)", "Student Action:", "3 car collision, vehicles blocking #2,3,4 lanes. Moderate injuries."))
						)
				),
				new Evaluations(
						new Array(
								new Evaluation("CMS", new Array("TargetCMS:", "95-NB5@EL TORO", "Sample Message:", "ACCIDENT AT HWY-55 / 3 RT LANES BLKD"))
						)
				)
		)
);
events.add(
		new Event(
				new Time(0, 24, 0), // orginally at 00:20:00 
				incidents.get(188), 
				new Properties(
						new Array(
								new Property("CHP Radio:", new Array("Dispatch:", "9-15, 9-19 Santa Lucia", "Field_1:", "Santa Lucia 9-15 go ahead.", "Field_2:", "Santa Lucia go ahead to 9-19.", "Dispatch:", "9-15 and 9-19 Santa Lucia 11-84 for 11-79 northbound 5 just north of Lake Forest Drive.", "Field_1:", "Santa Lucia 9-15 10-4. Enroute from 55 at 4th Street.", "Field_2:", "Santa Lucia 9-19 copied enroute from 405 at Culver.", "Dispatch:", "Santa Lucia copies 9-15 enroute from 55 at 4th and 9-19 enroute from 405 at Culver.")),
								new Property("CHP CAD:", new Array("Detail:", "9-15,9-19 ENRT FOR 1184")),
								new Property("Telephone Conversation:", new Array("Radio Reporter #3:", "Hello, this is Bill Bradley at KCTR radio.", "Radio Reporter #3:", "I heard here was a large collision on the 5.", "Radio Reporter #3:", "What details do you have on that?", "Student Action:", "Semi and 2 cars, tomatoes over three right lanes.", "Radio Reporter #3:", "Were there any injured?", "Student Action:", "Don't know yet."))
						)
				),
				new Evaluations(new Array())
		)
);
events.add(
		new Event(
				new Time(0, 26, 0), // orginally at 00:21:00 
				incidents.get(187), 
				new Properties(
						new Array(
								new Property("CHP Radio:", new Array("Dispatch:", "14-17 Santa Lucia information.", "Field:", "Santa Lucia 14-17 go ahead.", "Dispatch:", "14-17 Santa Lucia 10-39 Sablan's Towing.", "Field:", "14-17 copied 10-39 Sablan's Towing.")),
								new Property("CHP CAD:", new Array("Detail:", "1039 SABLAN TOWING"))
						)
				),
				new Evaluations(new Array())
		)
);
events.add(
		new Event(
				new Time(0, 28, 0), // orginally at 00:22:00 
				incidents.get(187), 
				new Properties(
						new Array(
								new Property("CHP Radio:", new Array("Field:", "Santa Lucia 14-S3", "Dispatch:", "14-S3 Santa Lucia go ahead.", "Field:", "Santa Lucia 10-97 northbound 405 at 55. Do you have an ETA for MAIT?", "Dispatch:", "14-S3 Santa Lucia 10-4 10-97 northbound 405 at 55. MAIT ETA 15 minutes.", "Field:", "Santa Lucia 14-S3 copies MAIT ETA 15.")),
								new Property("CHP CAD:", new Array("Detail:", "14-S3 1097 NB 405 @ 55", "Detail:", "MAIT ETA 15"))
						)
				),
				new Evaluations(new Array())
		)
);
events.add(
		new Event(
				new Time(0, 30, 0), // orginally at 00:27:00 
				incidents.get(187), 
				new Properties(
						new Array(
								new Property("CHP Radio:", new Array("Field:", "Santa Lucia 14-S3.", "Dispatch:", "14-S3 go ahead to Santa Lucia.", "Field:", "Santa Lucia, coroner 10-97. The injured have been transported.Santa Lucia, coroner 10-97. The injured have been transported.", "Dispatch:", "14-S3 Santa Lucia 10-4 coroner 10-97. Injured transported.")),
								new Property("CHP CAD:", new Array("Detail:", "CRNR 1097, INJURED TRANSPORTED"))
						)
				),
				new Evaluations(
						new Array(
								new Evaluation("Activity Log", new Array("Activity Log:", "Should enter info for 10-97 coroner"))
						)
				)
		)
);