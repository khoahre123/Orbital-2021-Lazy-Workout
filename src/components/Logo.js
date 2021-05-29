import React from "react";
import { StyleSheet, Image} from "react-native";

export default (props) => {
    let logoType = props.type;

    switch (logoType) {
        case ("mainLogo"):
            return (
                <Image
                    source={require("../assets/logo.png")}
                    style={styles.mainLogo} />
            );

        case ("tinyLogo"):
            return (
                <Image 
                    source={require("../assets/logo.png")}
                    style={styles.tinyLogo} />           
            );

        default:
            return null;
    }

}

const styles = StyleSheet.create({
    mainLogo: {
        flex: 0.5,
        width: 300,
        height: 300,
        alignSelf: "center"
    },

    tinyLogo: {
        width: 100,
        height: 100,
    }

});