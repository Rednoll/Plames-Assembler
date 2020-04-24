import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles';

const mainTheme = createMuiTheme({
	
	palette: {

		primary: { main: "#B0D5F2", contrastText: "white"},
		error: { main: "#FF1E45", contrastText: "white" },
		text: {

			primary: "#6D8593"
		}
	}
});

export default mainTheme;