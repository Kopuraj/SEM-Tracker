import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Header from '../src/Components/Header'
import Footer from '../src/Components/Footer'
import Homepage from '../src/Pages/homepage'
import Contactus from '../src/Pages/contactus'
import Loginpage from './Pages/loginpage';
import Aboutuspage from './Pages/aboutuspage';
import Timetablepage from './Pages/timetablepage';


function App() {
  return (
    <>
    <Router>
      <Header />
      <Routes>
        
          <Route path='/contactus' element={<Contactus />} />
          <Route path='/'  element={<Homepage/>} />
          <Route path='/loginpage' element={< Loginpage/>}/>
          <Route path='/aboutuspage' element={<Aboutuspage/>}/>
          <Route path='/timetablepage' element={< Timetablepage/>}/>
      </Routes>
       
      <Footer/>
    </Router>
     
      
  
    </>
  );
}
export default App;
