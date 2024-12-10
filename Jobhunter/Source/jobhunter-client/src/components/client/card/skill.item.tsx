import React from "react";
import styles from "styles/client.module.scss";

const SkillItem = (props: any) => {
  const { item } = props;
  return <div className={styles["skill-item"]}>{item}</div>;
};

export default SkillItem;
