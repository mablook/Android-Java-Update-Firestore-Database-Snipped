# Android-Java-Update-Firestore-Database-Snipped
# Created by Marcelo Bossle
Snipped to save information in firestore using java. It should be implemented in private or public functions depending on the need.

I need to save the information in Firestore but without creating a listener, access is restricted traffic is not important in this case.
Whenever the token changes, I need to save it for future access.
A snipped should be improved, but works very well for the concept.

see my idea for Swift https://github.com/mablook/Xcode-Swift-Update-Firestore-Database-Snipped



        mDatabase = FirebaseDatabase.getInstance().getReference();
        final Query query = mDatabase.child("user").child(uid).child("deviceGroup").child(androidId);


        final Query gQuery = mDatabase.child("user").child(uid);
        
        // Check if user exist
        gQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
            

                    Map<String, Object> map = new HashMap<>();
                    map.put("date", date);
                    map.put("androidUID", androidId);
                    map.put("manufacturer", manufacturer);
                    map.put("brand", brand);
                    map.put("model", model);
                    map.put("token", token);
            
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("create", date);
                    map1.put("emailverify", "false");
                    map1.put("useremail", femail);
                    map1.put("username", femail);
                    map1.put("userpass", "_look_");
            
            
                if(snapshot.exists()){

                    // Save info in deviceGroup to send notifications if token is refresh
                    mDatabase.child("user").child(uid).child("deviceGroup").child(androidId).setValue(map);

                }else{

                    mDatabase.child("user").child(uid).updateChildren(map1);
                    mDatabase.child("user").child(uid).child("deviceGroup").child(androidId).setValue(map);

                }
                
                
                
                
                
