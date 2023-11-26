class Pair implements Comparable<Pair>{
    public Pair (float insert_key, Object insert_value){
        key=insert_key;
        value=insert_value;

    }
    
    
    
    public float getKey() {
        return key;
    }
    public Object getValue(){
        return value;
    }
    @Override public int compareTo(Pair A){
        if(key==A.getKey()){
            return 0;
        }
        if(key>A.getKey()){
            return 1;

        }
        return -1;


    }

    public float key;
    public Object value;
}
