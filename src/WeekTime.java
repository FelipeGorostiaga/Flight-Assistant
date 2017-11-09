 public class WeekTime {

        private static final int DAY_TIME = 24 * 60;

        private int day;
        private int minute;

        public WeekTime(int day, int minute) {
            this.day = day;
            this.minute = minute;
        }

        public int getMinute() {
            return minute;
        }

        public int getDay(){
            return day;
        }

        public void setDay(int day){
            this.day = day;
        }
        public void setMinute(int minute){
            this.minute = minute;
        }

        //Se le pasa por parametros la cantidad de minutos y calcula el dia de la semana y minuto en que quedaria luego de transcurrido esos minutos.

        public void addMinutes(int mins) {

            if(mins <= 0)
                return;

            int aux;

            while( (getMinute() + mins)  >= DAY_TIME) {

                aux = DAY_TIME - getMinute();
                int day = getDay() + 1;

                if(day > 6) day = 0;

                setDay(day);
                mins -= aux;
                setMinute(0); //start of next day.

            }

            if(mins > 0) setMinute(getMinute() + mins);

        }

        public int calcMinutes(int day, int min) { //Calcula la cantidad de minutos desde este WeekTime al dia y minuto de la semana pasado por parametro.

            int weekDay = getDay();
            int minute = getMinute();
            int totalmin = 0;
            int aux;

            if(getDay() == day  && getMinute() == min)
                return 0;

            if(day == getDay() && min > getMinute())
                return min - getMinute();

            while (weekDay != day || min < minute) {

                aux = DAY_TIME - minute;
                totalmin += aux;
                weekDay++;
                minute = 0;

                if (weekDay > 6) weekDay = 0;

            }

            return totalmin + min;

        }
    }
