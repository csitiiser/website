import './App.css';
import vid from './assets/bg.mp4';

function App() {
  return (
      <video id="vid" muted autoPlay loop>
        <source src={vid} type="video/mp4" />
      </video>
  );
}

export default App;
