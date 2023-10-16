import "./App.css";
import vid from "./assets/bg.mp4";
import Navbar from "./components/navbar.jsx";

function App() {
  return (
    <div>
      <video id="vid" muted autoPlay loop>
        <source src={vid} type="video/mp4" />
      </video>
      <Navbar></Navbar>
    </div>
  );
}

export default App;
